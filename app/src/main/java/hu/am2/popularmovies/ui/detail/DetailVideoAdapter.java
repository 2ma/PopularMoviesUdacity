package hu.am2.popularmovies.ui.detail;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import hu.am2.popularmovies.R;
import hu.am2.popularmovies.data.repository.remote.model.VideoModel;

public class DetailVideoAdapter extends RecyclerView.Adapter<DetailVideoAdapter.VideoViewHolder> {

    private static final String YOUTUBE_THUMBNAIL_FRONT = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_BACK = "/0.jpg";

    private final LayoutInflater inflater;
    private final VideoClickListener listener;
    private List<VideoModel> videos = Collections.emptyList();

    public DetailVideoAdapter(LayoutInflater inflater, VideoClickListener listener) {
        this.inflater = inflater;
        this.listener = listener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.video_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bindMovie(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos == null ? 0 : videos.size();
    }

    public void setVideos(List<VideoModel> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public interface VideoClickListener {
        void onVideoClick(VideoModel video);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final ImageView thumbnail;

        public VideoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            itemView.setOnClickListener(this);
        }

        public void bindMovie(VideoModel video) {
            title.setText(video.getName());
            Glide.with(thumbnail).load(YOUTUBE_THUMBNAIL_FRONT + video.getKey() + YOUTUBE_THUMBNAIL_BACK)
                .into(thumbnail);
        }

        @Override
        public void onClick(View v) {
            listener.onVideoClick(videos.get(getAdapterPosition()));
        }
    }
}
