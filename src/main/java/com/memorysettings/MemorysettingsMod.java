package com.memorysettings;

import com.memorysettings.config.Configuration;
import com.memorysettings.event.ClientEventHandler;
import com.memorysettings.event.EventHandler;
import com.memorysettings.event.ModEventHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.management.ManagementFactory;
import java.util.Random;

import static com.memorysettings.MemorysettingsMod.MODID;
import static javax.swing.JOptionPane.VALUE_PROPERTY;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class MemorysettingsMod
{
    public static final  String        MODID                  = "memorysettings";
    public static final  Logger        LOGGER                 = LogManager.getLogger();
    private static final String        DISABLE_WARNING_BUTTON = "Stop showing";
    public static        Configuration config                 = new Configuration();
    public static        Random        rand                   = new Random();

    public MemorysettingsMod()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(ModEventHandler.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    public static void checkMemory()
    {
        if (config.getCommonConfig().disableWarnings.get())
        {
            return;
        }

        message("test");
        final int systemMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1048576);
        final int freeMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / 1048576);
        final int heapSetting = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576);

        if (System.getProperties().getProperty("sun.arch.data.model").equals("32") && systemMemory > 4096)
        {
            message("You're using 32bit java on a 64bit system, please install 64bit java.");
            return;
        }

        final int configMax = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().maximumClient.get() : config.getCommonConfig().maximumServer.get();
        final int configMin = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().minimumClient.get() : config.getCommonConfig().minimumServer.get();
        final int recommendMemory = (int) Math.min(((systemMemory * 0.7) * 0.8), Math.max(configMin, Math.min(configMax, freeMemory * 0.8)));

        String message = "";
        if (heapSetting > configMax)
        {
            message += "You have more memory allocated(" + heapSetting + "mb) than recommended for this pack, the maximum is: " + configMax
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
        }

        if (heapSetting < configMin)
        {

            message += "You have less memory allocated(" + heapSetting + "mb) than recommended for this pack, the minimum is: " + config.getCommonConfig().minimumClient.get()
                         + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
        }

        if (heapSetting > (recommendMemory + 523))
        {
            message += "You have more memory allocated than recommended for your system, the recommended amount for your system is: " + recommendMemory + " mb.\n";
        }

        if (recommendMemory < configMin)
        {
            message += "The recommended for your system is lower than the required minimum of " + config.getCommonConfig().minimumClient.get()
                         + "mb for this pack, things may not work out so well.\nMost common sign of insufficient ram is frequent stutters.\n";
        }

        if (message.equals(""))
        {
            return;
        }

        message(message);
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
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    String[] options = new String[] {"Ok", DISABLE_WARNING_BUTTON};
                    JFrame jf = new JFrame("Memory Settings");
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
                                config.getCommonConfig().disableWarnings.set(true);
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
                                 + config.getCommonConfig().helpfullinkmessage.get()
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

                    if (selectedValue instanceof String && selectedValue.equals("Stop showing"))
                    {
                        config.getCommonConfig().disableWarnings.set(true);
                    }
                });
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Error during showing memory warning GUI:", e);
        }

        LOGGER.error(s);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }
}
