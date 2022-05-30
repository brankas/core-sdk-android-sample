package com.brankas.testapp.adapter;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.brankas.testapp.R;
import com.brankas.testapp.custom.SvgDecoder;
import com.brankas.testapp.custom.SvgDrawableTranscoder;
import com.brankas.testapp.custom.SvgSoftwareLayerSetter;
import com.brankas.testapp.model.StatementBankItemViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StatementBanksAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private final Context context;
    private final List<StatementBankItemViewModel> banks;
    private final Boolean isCorporate;

    public StatementBanksAdapter(Context context, List<StatementBankItemViewModel> banks, Boolean isCorporate) {
        super();

        this.context = context;
        this.banks = banks;
        this.isCorporate = isCorporate;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatementBankItemViewHolder(inflater.inflate(R.layout.item_statement_bank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ArrayList<StatementBankItemViewModel> filtered = new ArrayList();
            for (StatementBankItemViewModel item : banks) {
                if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                    filtered.add(item);
            }

            StatementBankItemViewModel item = filtered.get(position);
            StatementBankItemViewHolder viewHolder = (StatementBankItemViewHolder) holder;
            viewHolder.checkbox.setChecked(item.isSelected());
            viewHolder.checkbox.setText(item.getBank().getTitle());
            viewHolder.checkbox.setOnClickListener(view -> {
                item.setSelected(!item.isSelected());
            });

            if (item.getBank().getLogoUrl().endsWith(".svg")) {
                Glide.with(context)
                        .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                        .from(Uri.class)
                        .as(SVG.class)
                        .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                        .sourceEncoder(new StreamEncoder())
                        .cacheDecoder(new FileToStreamDecoder(new SvgDecoder()))
                        .decoder(new SvgDecoder())
                        .placeholder(R.drawable.ic_banking)
                        .listener(new SvgSoftwareLayerSetter())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .load(Uri.parse(item.getBank().getLogoUrl()))
                        .into(viewHolder.imageView);
            } else {
                Glide.with(context)
                        .load(item.getBank().getLogoUrl())
                        .placeholder(R.drawable.ic_banking)
                        .into(viewHolder.imageView);
            }

        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (StatementBankItemViewModel item : banks) {
            if (null != item.getBank() && item.getBank().isCorporate() == isCorporate)
                count ++;
        }
        return count;
    }

    private class StatementBankItemViewHolder extends RecyclerView.ViewHolder{
        AppCompatCheckBox checkbox;
        ImageView imageView;

        public StatementBankItemViewHolder(View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.checkbox);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
