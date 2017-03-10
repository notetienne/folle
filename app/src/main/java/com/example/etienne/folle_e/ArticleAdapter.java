package com.example.etienne.folle_e;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Produit> {


    //tweets est la liste des models à afficher
    public ArticleAdapter(Context context, List<Produit> tweets) {
        super(context, 0, tweets);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_article,parent, false);
        }

        ProduitViewHolder viewHolder = (ProduitViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ProduitViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
            viewHolder.prix = (TextView) convertView.findViewById(R.id.prix_art);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Produit article = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.nom.setText(article.getNom());
        viewHolder.prix.setText(article.getPrix());
        viewHolder.image.setImageDrawable(article.getImage());

        return convertView;
    }

    private class ProduitViewHolder{
        public TextView nom;
        public TextView prix;
        public ImageView image;
    }
}
