package net.schwarzbaer.java.tools.imageserver.data;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public record FileOutput(
		@NonNull  String fileName,
		@Nullable String comment,
		@NonNull  FileData.ImageFormat imageFormat
) {}
