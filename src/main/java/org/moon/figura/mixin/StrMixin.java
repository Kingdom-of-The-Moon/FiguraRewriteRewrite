package org.moon.figura.mixin;

import org.luaj.vm2.ast.Str;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;

@Mixin(value = Str.class, remap = false)
public class StrMixin {
	@Inject(
			method = "unquote",
			at = @At("HEAD"),
			cancellable = true
	) private static void unquote(String s, CallbackInfoReturnable<byte[]> info){
		StringBuilder builder = new StringBuilder();
		char[] c = s.toCharArray();
		int n = c.length;
		for (int i = 0; i < n; i++) {
			if (c[i] == '\\') {
				switch (c[++i]) {
					case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
						int d = c[i++] - '0';
						for (int j = 0; i < n && j < 2 && c[i] >= '0' && c[i] <= '9'; i++, j++)
							d = d * 10 + c[i] - '0';
						builder.append((char) d);
						--i;
					}
					case 'a' -> builder.append((char) 7);
					case 'b' -> builder.append('\b');
					case 'f' -> builder.append('\f');
					case 'n' -> builder.append('\n');
					case 'r' -> builder.append('\r');
					case 't' -> builder.append('\t');
					case 'v' -> builder.append((char) 11);
					case '"' -> builder.append('"');
					case '\'' -> builder.append('\'');
					case '\\' -> builder.append('\\');
					default -> builder.append(c[i]);
				}
			} else {
				builder.append(c[i]);
			}
		}
		info.setReturnValue(builder.toString().getBytes(StandardCharsets.UTF_8));
	}
}
