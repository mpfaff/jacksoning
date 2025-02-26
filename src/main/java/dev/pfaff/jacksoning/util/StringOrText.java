package dev.pfaff.jacksoning.util;

import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Only one will be non-null at a time.
 */
public record StringOrText(@Nullable String string, @Nullable Text text) {
	public static final StringOrText[] EMPTY_ARRAY = new StringOrText[0];

	public StringOrText {
		if (string == null && text == null) throw new NullPointerException();
		assert text == null || asLiteral(text) == null : "You used the internal constructor, didn't you?";
	}

	public Text asText() {
		return text != null ? text : Text.of(string);
	}

	public static StringOrText of(@NotNull String string) {
		return new StringOrText(Objects.requireNonNull(string), null);
	}

	public static @Nullable String asLiteral(@NotNull Text text) {
		if (text.getContent() instanceof PlainTextContent.Literal(String string) && text.getStyle().isEmpty() && text.getSiblings().isEmpty()) {
			return string;
		} else {
			return null;
		}
	}

	public static StringOrText of(@NotNull Text text) {
		var s = asLiteral(text);
		if (s != null) return of(s);
		return new StringOrText(null, Objects.requireNonNull(text));
	}
}
