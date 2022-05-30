package com.brankas.testapp.custom;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
    @Override
    public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
        try {
            return new SimpleResource(SVG.getFromInputStream(source));
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }

    @Override
    public String getId() {
        return "SvgDecoder.com.brankas.tap.app.custom";
    }
}
