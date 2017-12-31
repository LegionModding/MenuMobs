package superbas11.menumobs;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import superbas11.menumobs.gui.BlacklistArrayEntry;
import superbas11.menumobs.gui.FixedMobEntry;
import superbas11.menumobs.gui.VolumeSliderEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.config.Property.Type.STRING;

@Mod(
        modid = Reference.MODID,
        name = Reference.NAME,
        version = "@MOD_VERSION@",
        useMetadata = true,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        guiFactory = Reference.GUI_FACTORY,
        acceptedMinecraftVersions = "[1.12]")
public class MenuMobs {
    @Instance(value = Reference.MODID)
    public static MenuMobs instance;
    public boolean showMainMenuMobs = true;

    public MainMenuRenderTicker mainMenuTicker;
    public boolean showOnlyPlayerModels;
    public double mobSoundVolume;
    public String[] fixedMob;
    public Property blacklist;
    public boolean allowDebugOutput;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File file = event.getSuggestedConfigurationFile();
        Reference.config = new Configuration(file);
        syncConfig();
    }

    public void syncConfig() {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        Reference.config.load();

        Reference.config.setCategoryComment(ctgyGen, "ATTENTION: Editing this file manually is no longer necessary. \n" +
                                                     "On the Mods list screen select the entry for menumobs, then click the Config button to modify these settings.");

        List<String> orderedKeys = new ArrayList<String>(ConfigElements.values().length);

        showMainMenuMobs = Reference.config.getBoolean(ConfigElements.SHOW_MAIN_MENU_MOBS.key(), ctgyGen, true, ConfigElements.SHOW_MAIN_MENU_MOBS.desc(), ConfigElements.SHOW_MAIN_MENU_MOBS.languageKey());
        orderedKeys.add(ConfigElements.SHOW_MAIN_MENU_MOBS.key());
        showOnlyPlayerModels = Reference.config.getBoolean(ConfigElements.SHOW_ONLY_PLAYER_MODELS.key(), ctgyGen, false, ConfigElements.SHOW_ONLY_PLAYER_MODELS.desc(), ConfigElements.SHOW_ONLY_PLAYER_MODELS.languageKey());
        orderedKeys.add(ConfigElements.SHOW_ONLY_PLAYER_MODELS.key());
        mobSoundVolume = Reference.config.get(ctgyGen, ConfigElements.MOB_SOUNDS_VOLUME.key(), 0.5f, ConfigElements.MOB_SOUNDS_VOLUME.desc(), 0.0F, 1.0F).setConfigEntryClass(VolumeSliderEntry.class).getDouble();
        orderedKeys.add(ConfigElements.MOB_SOUNDS_VOLUME.key());
        fixedMob = Reference.config.get(ctgyGen, ConfigElements.FIXED_MOB.key(), new String[]{}, ConfigElements.FIXED_MOB.desc(), STRING).setConfigEntryClass(FixedMobEntry.class).getStringList();
        orderedKeys.add(ConfigElements.FIXED_MOB.key());
        blacklist = Reference.config.get(ctgyGen, ConfigElements.BLACKLIST.key(), new String[]{}, ConfigElements.BLACKLIST.desc(), STRING).setArrayEntryClass(BlacklistArrayEntry.class);
        orderedKeys.add(ConfigElements.BLACKLIST.key());
        allowDebugOutput = Reference.config.getBoolean(ConfigElements.ALLOW_DEBUG_OUTPUT.key(), ctgyGen, false, ConfigElements.SHOW_MAIN_MENU_MOBS.desc(), ConfigElements.ALLOW_DEBUG_OUTPUT.languageKey());
        orderedKeys.add(ConfigElements.ALLOW_DEBUG_OUTPUT.key());

        Reference.config.setCategoryPropertyOrder(ctgyGen, orderedKeys);

        Reference.config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        mainMenuTicker = new MainMenuRenderTicker();
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MODID)) {
            Reference.config.save();
            syncConfig();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (showMainMenuMobs)
            if (mainMenuTicker.isMainMenu(event.getGui()))
                mainMenuTicker.register();
            else if (mainMenuTicker.isRegistered())
                mainMenuTicker.unRegister();
    }
}
