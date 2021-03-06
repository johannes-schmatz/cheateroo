package local.pixy.cheateroo.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;

import local.pixy.cheateroo.Cheateroo;

public enum FeatureToggle implements IHotkeyTogglable, IConfigNotifiable<IConfigBoolean> {
	AUTO_CRYTAL("autoCrytal", false, false, "", "Toggles automatic end crytal destruction", "Auto Crystal"),
	AUTO_TOTEM("autoTotem", false, false, "",
			"Toggles Automatic offhand totem, works only if player is not in creative.", "Auto Totem"),
	CHEAT_FLY("cheatFly", false, false, "", "Toggles the elytra fly hack.", "Cheat Fly"),
	HIGHLIGHT_BLOCK("highlightBlock", false, false, "Highlights a block thought walls", "X-Ray"),
	ENABLE_RENDERING("toggleRendering", true, false, "P,R", "Toggles all rendering", "Rendering"),
	OVERRIDE_CHRISTMAS_CHEST("christmasChestEnabled", false, false, "",
			"Toggles the overwriting of the chest render to use the value from christmasChest for ",
			"Enable Christmas Chest"),
	OVERRIDE_CHRISTMAS_CHEST_VALUE("christmasChest", false, false, "", "True leads to gift chests.",
			"Christmas Chest Texture"),
	DISABLE_NARRATOR_BUTTON("disableNarratorButton", false, false, "",
			"Turns off the annoying Ctrl+B narrator cycling.", "Disable Narrator Cycle Button"),
	BUCKET_FILLER("bucketFiller", false, false, "", "Automaticly fills buckets if you get a fluid in your crosshair.",
			"Bucket Fill"),
	DISPLAY_PLAYER_HEALTH("displayPlayerHealth", false, false, "",
			"Renders the health of the player after the name, in color.", "Player Health Display");

	private final String name;
	private final String comment;
	private final String prettyName;
	private final IKeybind keybind;
	private final boolean defaultValueBoolean;
	private final boolean singlePlayer;
	private boolean valueBoolean;
	private IValueChangeCallback<IConfigBoolean> callback;

	private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment) {
		this(name, defaultValue, false, defaultHotkey, KeybindSettings.DEFAULT, comment);
	}

	private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey,
			String comment) {
		this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment);
	}

	private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings,
			String comment) {
		this(name, defaultValue, false, defaultHotkey, settings, comment);
	}

	private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey,
			KeybindSettings settings, String comment) {
		this(name, defaultValue, singlePlayer, defaultHotkey, settings, comment,
				StringUtils.splitCamelCase(name.substring(5)));
	}

	private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName) {
		this(name, defaultValue, false, defaultHotkey, comment, prettyName);
	}

	private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment,
			String prettyName) {
		this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName);
	}

	private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey,
			KeybindSettings settings, String comment, String prettyName) {
		this.name = name;
		this.valueBoolean = defaultValue;
		this.defaultValueBoolean = defaultValue;
		this.singlePlayer = singlePlayer;
		this.comment = comment;
		this.prettyName = prettyName;
		this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
		this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
	}

	@Override
	public ConfigType getType() {
		return ConfigType.HOTKEY;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getConfigGuiDisplayName() {
		if (this.singlePlayer) {
			return GuiBase.TXT_GOLD + this.getName() + GuiBase.TXT_RST;
		}

		return this.getName();
	}

	@Override
	public String getPrettyName() {
		return this.prettyName;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(this.valueBoolean);
	}

	@Override
	public String getDefaultStringValue() {
		return String.valueOf(this.defaultValueBoolean);
	}

	@Override
	public void setValueFromString(String value) {
	}

	@Override
	public void onValueChanged() {
		if (this.callback != null) {
			this.callback.onValueChanged(this);
		}
	}

	@Override
	public void setValueChangeCallback(IValueChangeCallback<IConfigBoolean> callback) {
		this.callback = callback;
	}

	@Override
	public String getComment() {
		if (this.comment == null) {
			return "";
		}

		if (this.singlePlayer) {
			return this.comment + "\n" + StringUtils.translate("cheateroo.label.config_comment.single_player_only");
		} else {
			return this.comment;
		}
	}

	@Override
	public IKeybind getKeybind() {
		return this.keybind;
	}

	@Override
	public boolean getBooleanValue() {
		return this.valueBoolean;
	}

	@Override
	public boolean getDefaultBooleanValue() {
		return this.defaultValueBoolean;
	}

	@Override
	public void setBooleanValue(boolean value) {
		boolean oldValue = this.valueBoolean;
		this.valueBoolean = value;

		if (oldValue != this.valueBoolean) {
			this.onValueChanged();
		}
	}

	@Override
	public boolean isModified() {
		return this.valueBoolean != this.defaultValueBoolean;
	}

	@Override
	public boolean isModified(String newValue) {
		return Boolean.parseBoolean(newValue) != this.defaultValueBoolean;
	}

	@Override
	public void resetToDefault() {
		this.valueBoolean = this.defaultValueBoolean;
	}

	@Override
	public JsonElement getAsJsonElement() {
		return new JsonPrimitive(this.valueBoolean);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element) {
		try {
			if (element.isJsonPrimitive()) {
				this.valueBoolean = element.getAsBoolean();
			} else {
				Cheateroo.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(),
						element);
			}
		} catch (Exception e) {
			Cheateroo.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(),
					element, e);
		}
	}
}
