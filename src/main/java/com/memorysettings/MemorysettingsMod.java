package com.memorysettings;

import com.memorysettings.config.Configuration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.memorysettings.Memory.heapSetting;
import static com.memorysettings.Memory.systemMemory;
import static com.memorysettings.MemorysettingsMod.MODID;
import static javax.swing.JOptionPane.VALUE_PROPERTY;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class MemorysettingsMod
{
    public static final  String           MODID                  = "memorysettings";
    public static final  Logger           LOGGER                 = LogManager.getLogger();
    private static final String           DISABLE_WARNING_BUTTON = "Stop showing";
    public static        Configuration    config                 = new Configuration();
    public static        Random           rand                   = new Random();
    public static        MutableComponent memorycheckresult      = Component.empty();
    public static        boolean          didDisplay             = false;

    public MemorysettingsMod()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));

        config.load();
        if (!config.getCommonConfig().disableWarnings)
        {
            doWarning();
        }
    }

    public static void doWarning()
    {
        if (System.getProperties().getProperty("sun.arch.data.model").equals("32") && systemMemory > 4096)
        {
            memorycheckresult.append(Component.translatable("warning.32bit"));
            LOGGER.warn("You're using 32bit java on a 64bit system, please install 64bit java.");
            return;
        }

        final int configMax = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().maximumClient : config.getCommonConfig().maximumServer;
        final int configMin = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().minimumClient : config.getCommonConfig().minimumServer;
        final int recommendMemory = Math.min(getRecommendedMemoryForSystemMemory(systemMemory), configMax);

        String message = "";
        if (heapSetting > configMax)
        {
            message += "You have more memory allocated(" + heapSetting + "mb) than recommended for this pack, the maximum is: " + configMax
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable("warning.toomuch",
              Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
              Component.literal(configMax + "").withStyle(ChatFormatting.BLUE),
              Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (heapSetting < configMin)
        {
            message += "You have less memory allocated(" + heapSetting + "mb) than recommended for this pack, the minimum is: " + configMin
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable("warning.toolow",
              Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
              Component.literal(configMin + "").withStyle(ChatFormatting.BLUE),
              Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if ((Math.abs(heapSetting - recommendMemory) / (double) recommendMemory) * 100 > config.getCommonConfig().warningTolerance && !(heapSetting > configMax
                                                                                                                                          || heapSetting < configMin))
        {
            message += "You have " + (heapSetting > recommendMemory ? "more" : "less")
                         + " more memory allocated than recommended for your system, the recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable(heapSetting > recommendMemory ? "warning.overrecommended" : "warning.underrecommended",
              Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
              Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (recommendMemory < configMin)
        {
            message += "The recommended for your system is lower than the required minimum of " + configMin
                         + "mb for this pack, things may not work out so well.\nMost common sign of insufficient ram is frequent stutters.\n";
            memorycheckresult.append(Component.translatable("warning.recommendedbelowmin",
              Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN),
              Component.literal(configMin + "").withStyle(ChatFormatting.RED)));
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
            if (FMLEnvironment.dist != DEDICATED_SERVER)
            {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.contains("mac")) || (OS.contains("darwin")))
                {
                    //openMessageUI(s);
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

    public static int getRecommendedMemoryForSystemMemory(final int systemMemory)
    {
        Map.Entry<Integer, Integer> lastEntry = null;
        int recommendedMemory = 0;
        for (final Map.Entry<Integer, Integer> dataEntry : config.getCommonConfig().recommendedMemory.entrySet())
        {
            if (systemMemory > dataEntry.getKey())
            {
                lastEntry = dataEntry;
            }
            else
            {
                if (lastEntry == null)
                {
                    lastEntry = dataEntry;
                    break;
                }

                double percent = (systemMemory - lastEntry.getKey()) / (Math.max(1d, dataEntry.getKey() - lastEntry.getKey()));

                return (int) (lastEntry.getValue() + percent * (dataEntry.getValue() - lastEntry.getValue()));
            }
        }

        if (lastEntry != null)
        {
            return lastEntry.getValue();
        }

        return recommendedMemory;
    }
}
