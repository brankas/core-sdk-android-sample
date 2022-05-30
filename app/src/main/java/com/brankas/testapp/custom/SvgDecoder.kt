package com.brankas.testapp.custom

import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream


class SvgDecoder : ResourceDecoder<InputStream, SVG> {

    override fun decode(source: InputStream?, width: Int, height: Int): Resource<SVG> {
        return try {
            val svg = SVG.getFromInputStream(source)
            SimpleResource(svg)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }
    }

    override fun getId(): String {
        return "SvgDecoder.com.brankas.tap.app.custom";
    }

}
