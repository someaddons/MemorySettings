package com.memorysettings;

import com.memorysettings.config.Configuration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.Random;

import static javax.swing.JOptionPane.VALUE_PROPERTY;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import static net.fabricmc.api.EnvType.SERVER;

// The value here should match an entry in the META-INF/mods.toml file
public class MemorysettingsMod implements ModInitializer
{
    public static final  String           MODID                  = "memorysettings";
    public static final  Logger           LOGGER                 = LogManager.getLogger();
    private static final String           DISABLE_WARNING_BUTTON = "Stop showing";
    public static        Configuration    config                 = new Configuration();
    public static        Random           rand                   = new Random();
    public static        MutableComponent memorycheckresult      = new TextComponent("");

    public MemorysettingsMod()
    {

    }

    @Override
    public void onInitialize()
    {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
    }

    public static void checkMemory()
    {
        config = new Configuration();
        config.load();

        if (config.getCommonConfig().disableWarnings)
        {
            return;
        }

        final int systemMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1048576);
        final int freeMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / 1048576);
        final int heapSetting = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576);

        if (System.getProperties().getProperty("sun.arch.data.model").equals("32") && systemMemory > 4096)
        {
            memorycheckresult.append(new TranslatableComponent("warning.32bit"));
            LOGGER.warn("You're using 32bit java on a 64bit system, please install 64bit java.");
            return;
        }


        final int configMax = (FabricLoader.getInstance().getEnvironmentType() != SERVER) ? config.getCommonConfig().maximumClient : config.getCommonConfig().maximumServer;
        final int configMin = (FabricLoader.getInstance().getEnvironmentType() != SERVER) ? config.getCommonConfig().minimumClient : config.getCommonConfig().minimumServer;
        final int recommendMemory = (int) Math.min(((systemMemory * 0.7) * 0.8), Math.max(configMin, Math.min(configMax, freeMemory * 0.8)));

        String message = "";
        if (heapSetting > configMax)
        {
            message += "You have more memory allocated(" + heapSetting + "mb) than recommended for this pack, the maximum is: " + configMax
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(new TranslatableComponent("warning.toomuch",
              new TextComponent(heapSetting + "").withStyle(ChatFormatting.YELLOW),
              new TextComponent(configMax + "").withStyle(ChatFormatting.BLUE),
              new TextComponent(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (heapSetting < configMin)
        {
            message += "You have less memory allocated(" + heapSetting + "mb) than recommended for this pack, the minimum is: " + configMin
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(new TranslatableComponent("warning.toolow",
              new TextComponent(heapSetting + "").withStyle(ChatFormatting.YELLOW),
              new TextComponent(configMin + "").withStyle(ChatFormatting.BLUE),
              new TextComponent(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (heapSetting > (recommendMemory + 550))
        {
            message += "You have more memory allocated than recommended for your system, the recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(new TranslatableComponent("warning.overrecommended", new TextComponent(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (recommendMemory < configMin)
        {
            message += "The recommended for your system is lower than the required minimum of " + configMin
                         + "mb for this pack, things may not work out so well.\nMost common sign of insufficient ram is frequent stutters.\n";
            memorycheckresult.append(new TranslatableComponent("warning.recommendedbelowmin",
              new TextComponent(recommendMemory + "").withStyle(ChatFormatting.GREEN),
              new TextComponent(configMin + "").withStyle(ChatFormatting.RED)));
        }

        if (message.equals(""))
        {
            return;
        }

        LOGGER.warn(message);

        if (heapSetting < 1025)
        {
            message(message);
        }
    }

    /**
     * Message to log and gui if existing
     *
     * @param s
     */
    private static void message(final String s)
    {
        try
        {
            if (FabricLoader.getInstance().getEnvironmentType() != SERVER)
            {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.contains("mac")) || (OS.contains("darwin")))
                {
                    openMessageUI(s);
                }
                else
                {
                    javax.swing.SwingUtilities.invokeLater(() ->
                    {
                        openMessageUI(s);
                    });
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Error during showing memory warning GUI:", e);
        }

        LOGGER.error(s);
    }

    private static void openMessageUI(final String s)
    {
        String[] options = new String[] {"Ok", DISABLE_WARNING_BUTTON};
        JFrame jf = new JFrame("Memory Settings");
        jf.setResizable(false);
        jf.setAlwaysOnTop(true);
        JPanel panel = new JPanel();
        //panel.setBackground(Color.GRAY);
        jf.setContentPane(panel);

        JLabel label = new JLabel();
        Font font = label.getFont();
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        JOptionPane optionPane = new JOptionPane();

        optionPane.setMessage(s);
        optionPane.setOptions(options);

        optionPane.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
        {
            @Override
            public void propertyChange(final PropertyChangeEvent evt)
            {
                if (evt.getNewValue().equals(DISABLE_WARNING_BUTTON))
                {
                    config.getCommonConfig().disableWarnings = true;
                    config.save();
                }

                jf.dispose();
            }
        });

        JEditorPane ep = new JEditorPane("text/html", "");

        panel.add(optionPane, BorderLayout.CENTER);
        optionPane.add(ep, BorderLayout.CENTER);

        ep.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        ep.setEditable(false);

        ep.setText("<html><body style=\"" + style + "\">" //
                     + config.getCommonConfig().howtolink
                     + "</body></html>");

        ep.addHyperlinkListener(event -> {
            if (event.getEventType() == ACTIVATED)
            {
                try
                {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                }
                catch (Exception ex)
                {
                    LOGGER.warn("error:", ex);
                }
            }
        });

        //ep.setToolTipText("if you click on <b>that link you go to     the stack");

        jf.setSize(new Dimension(Math.max(jf.getPreferredSize().width, ep.getPreferredSize().width), jf.getPreferredSize().height + ep.getPreferredSize().height));
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);

        Object selectedValue = optionPane.getValue();

        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.pack();

        if (selectedValue instanceof String && selectedValue.equals("Stop showing"))
        {
            config.getCommonConfig().disableWarnings = true;
            config.save();
        }
    }
}
