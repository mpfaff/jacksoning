package dev.pfaff.jacksoning.util;

import net.minecraft.text.LiteralTextContent;
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
		assert text == null || !isLiteral(text) : "You used the internal constructor, didn't you?";
	}

	public static StringOrText of(@NotNull String string) {
		return new StringOrText(Objects.requireNonNull(string), null);
	}

	public static boolean isLiteral(@NotNull Text text) {
		return text.getContent() instanceof LiteralTextContent && text.getStyle().isEmpty() && text.getSiblings().isEmpty();
	}

	public static StringOrText of(@NotNull Text text) {
		if (isLiteral(text)) {
			return of(((LiteralTextContent) text.getContent()).string());
		}
		return new StringOrText(null, Objects.requireNonNull(text));
	}
}
