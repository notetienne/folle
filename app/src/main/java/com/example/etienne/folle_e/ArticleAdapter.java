package com.example.etienne.folle_e;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Produit> {

    private ArrayList<ProduitAdapterListener> mListListener = new ArrayList<ProduitAdapterListener>();
    private List<Produit> mListP;

    //tweets est la liste des models à afficher
    public ArticleAdapter(Context context, List<Produit> tweets) {
        super(context, 0, tweets);
        this.mListP = tweets;
    }

    public void addListeners(ProduitAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Produit item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickNom(item, position);
        }
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
        viewHolder.image.setImageDrawable(article.getImage());//(new ColorDrawable(article.getImage()));

        //------------ Début de l'ajout -------
//On mémorise la position de la "Personne" dans le composant textview
        viewHolder.image.setTag(position);
//On ajoute un listener
        viewHolder.image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Lorsque l'on clique sur le nom, on récupère la position de la "Personne"
                Integer position = (Integer)v.getTag();

                //On prévient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendListener(mListP.get(position), position);

            }

        });
//------------ Fin de l'ajout -------

        return convertView;
    }

    private class ProduitViewHolder{
        public TextView nom;
        public TextView prix;
        public ImageView image;
    }

    public interface ProduitAdapterListener {
        public void onClickNom(Produit item, int position);
    }
}
