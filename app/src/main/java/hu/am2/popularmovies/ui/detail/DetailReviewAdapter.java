package hu.am2.popularmovies.ui.detail;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.am2.popularmovies.R;
import hu.am2.popularmovies.data.repository.remote.model.ReviewModel;

public class DetailReviewAdapter extends RecyclerView.Adapter<DetailReviewAdapter.ReviewViewHolder> {

    private final LayoutInflater inflater;
    private List<ReviewModel> reviews = Collections.emptyList();

    public DetailReviewAdapter(LayoutInflater inflater, List<ReviewModel> reviews) {
        this.inflater = inflater;
        this.reviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.review_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bindReview(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView reviewText, authorText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.review);
            authorText = itemView.findViewById(R.id.author);
        }

        public void bindReview(ReviewModel review) {
            reviewText.setText(review.getContent());
            authorText.setText(review.getAuthor());
        }
    }
}
