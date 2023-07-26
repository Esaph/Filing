package esaph.filing.Board.BoardManager;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;
import esaph.filing.R;
import esaph.filing.TourenPlaner.TMSDestinationAdress.model.MostUsedAdress;

public class AdapterPrivayChoose extends ArrayAdapter
{
    private LayoutInflater inflater;
    private List<BoardPolicyItem> list;

    public AdapterPrivayChoose(@NonNull Context context, int resource, List<BoardPolicyItem> list) {
        super(context, resource, list);
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    private class ViewHolderPrivacyChoose
    {
        private TextView textViewPrivacyPolicy;
        private TextView textViewBeschreibung;
        private ImageView imageViewPolicy;
    }


    private View getSpinnerLayout(int position, View convertView, ViewGroup parent)
    {
        BoardPolicyItem boardPolicyItem = list.get(position);
        ViewHolderPrivacyChoose viewHolderPrivacyChoose;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_sichtbarkeitsmodus, parent, false);
            viewHolderPrivacyChoose = new ViewHolderPrivacyChoose();
            viewHolderPrivacyChoose.textViewPrivacyPolicy = convertView.findViewById(R.id.item_sichtbarkeitsmodus_TextViewPolicy);
            viewHolderPrivacyChoose.textViewBeschreibung = convertView.findViewById(R.id.item_sichtbarkeitsmodus_TextViewPolicyDescription);
            viewHolderPrivacyChoose.imageViewPolicy = convertView.findViewById(R.id.item_sicherbarkeitsmodus_ImageViewModus);
            convertView.setTag(viewHolderPrivacyChoose);
        }
        else
        {
            viewHolderPrivacyChoose = (ViewHolderPrivacyChoose) convertView.getTag();
        }


        viewHolderPrivacyChoose.textViewBeschreibung.setText(boardPolicyItem.getmPolicyDescription());
        viewHolderPrivacyChoose.textViewPrivacyPolicy.setText(boardPolicyItem.getmPolicyName());

        Glide.with(viewHolderPrivacyChoose.imageViewPolicy.getContext())
                .load(boardPolicyItem.getmImgId())
                .into(viewHolderPrivacyChoose.imageViewPolicy);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getSpinnerLayout(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getSpinnerLayout(position, convertView, parent);
    }
}