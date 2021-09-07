package com.codingstuff.instag.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingstuff.instag.Model.Comment;
import com.codingstuff.instag.Model.Users;
import com.codingstuff.instag.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    List<Comment> commentList;
    List<Users> usersList;
    Activity context;
    String postId;
    private FirebaseFirestore firestore;
    public CommentAdapter(List<Comment> commentList, Activity context, List<Users> usersList , String postId){
        this.commentList = commentList;
        this.context = context;
        this.usersList = usersList;
        this.postId = postId;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.each_comment , parent , false);
       firestore = FirebaseFirestore.getInstance();
       return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.setComment_text(comment.getComment());
        String profileUrl = usersList.get(position).getImage();
        holder.setProfile_pic(profileUrl);

        String commentId = comment.CommentId;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(comment.getUser())){
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setClickable(true);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     firestore.collection("Posts/" + postId + "/comment").document(currentUserId).delete()
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                     }else{
                                         Toast.makeText(context, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                     }
                                  }
                              });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private ImageView profile_pic;
        private TextView comment_text;
        private ImageButton deleteBtn;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            deleteBtn = mView.findViewById(R.id.imageButton2);
        }

        public void setProfile_pic(String url){
            profile_pic = mView.findViewById(R.id.profile_pic_comment);
            Glide.with(context).load(url).into(profile_pic);
        }
        public void setComment_text(String text){
            comment_text = mView.findViewById(R.id.comment_textview);
            comment_text.setText(text);
        }
    }
}
