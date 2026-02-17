package me.lucko.minichatformatter;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MiniChatFormatterConfig {

    public static final BuilderCodec<MiniChatFormatterConfig> CODEC = BuilderCodec.builder(MiniChatFormatterConfig.class, MiniChatFormatterConfig::new)
            .append(
                    new KeyedCodec<>("Format", Codec.STRING),
                    MiniChatFormatterConfig::setFormat,
                    MiniChatFormatterConfig::getFormat
            )
            .add()
            .build();

    private String format = "<username>: <message>";

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
