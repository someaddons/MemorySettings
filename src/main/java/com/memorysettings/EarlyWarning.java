package com.memorysettings;

import com.memorysettings.config.CommonConfiguration;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;

public class EarlyWarning
{// TODO: Alternatively prevent all other resources(mods/resourcepacks etc) from loading to be able to load vanilla minecraft with the error message screen

    public static boolean showEarlyScreenFor(final String message, final URI link)
    {
        final boolean result = TinyFileDialogs.tinyfd_messageBox(
            "Memory Settings",
            message,
            "okcancel",
            "info",
            true);

        if (result)
        {
            if (link != null)
            {
                getPlatform().openUri(link);
            }
            Runtime.getRuntime().exit(0);
        }

        return result;
    }

    public static OS getPlatform()
    {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (s.contains("win"))
        {
            return OS.WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return OS.OSX;
        }
        else if (s.contains("solaris"))
        {
            return OS.SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return OS.SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return OS.LINUX;
        }
        else
        {
            return s.contains("unix") ? OS.LINUX : OS.UNKNOWN;
        }
    }

    public static enum OS
    {
        LINUX,
        SOLARIS,
        WINDOWS
            {
                @Override
                protected String[] getOpenUriArguments(URI uri)
                {
                    return new String[] {"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
                }
            },
        OSX
            {
                @Override
                protected String[] getOpenUriArguments(URI uri)
                {
                    return new String[] {"open", uri.toString()};
                }
            },
        UNKNOWN;

        OS()
        {
        }

        public void openUri(URI uri)
        {
            try
            {
                Process process = AccessController.doPrivileged(
                    (PrivilegedExceptionAction<Process>) (() -> Runtime.getRuntime().exec(this.getOpenUriArguments(uri)))
                );
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            }
            catch (IOException | PrivilegedActionException privilegedactionexception)
            {
                CommonConfiguration.LOGGER.error("Couldn't open location '{}'", uri, privilegedactionexception);
            }
        }

        public void openFile(File file)
        {
            this.openUri(file.toURI());
        }

        public void openPath(Path path)
        {
            this.openUri(path.toUri());
        }

        protected String[] getOpenUriArguments(URI uri)
        {
            String s = uri.toString();
            if ("file".equals(uri.getScheme()))
            {
                s = s.replace("file:", "file://");
            }

            return new String[] {"xdg-open", s};
        }

        public void openUri(String uri)
        {
            try
            {
                this.openUri(new URI(uri));
            }
            catch (IllegalArgumentException | URISyntaxException urisyntaxexception)
            {
                CommonConfiguration.LOGGER.error("Couldn't open uri '{}'", uri, urisyntaxexception);
            }
        }
    }
}
