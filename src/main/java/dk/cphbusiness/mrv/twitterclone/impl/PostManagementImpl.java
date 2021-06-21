package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.PostManagement;
import dk.cphbusiness.mrv.twitterclone.dto.Post;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostManagementImpl implements PostManagement {
    private Jedis jedis;
    private Time time;

    public PostManagementImpl(Jedis jedis, Time time) {
        this.jedis = jedis;
        this.time = time;
    }

    @Override
    public boolean createPost(String username, String message) {
        if (jedis.mset("username", username, "message", message) == "OK")
        {
            jedis.mset("username", username, "message", message);
        }

        else
            {
            return false;
        }
        return true;
    }

    @Override
    public List<Post> getPosts(String username) {

        var posts =new ArrayList<Post>();

        for ( Tuple p:jedis.zrangeWithScores("posts",0,jedis.zcard("posts") )
             ) {
            var post = new Post((long) p.getScore(),p.getElement());
            posts.add(post);
        }

return posts;
        
    }

    @Override
    public List<Post> getPostsBetween(String username, long timeFrom, long timeTo) {
        var posts =new ArrayList<Post>();

        for ( Tuple p:jedis.zrangeWithScores("posts",0,jedis.zcard("posts") )
        ) {
            var post = new Post((long) p.getScore(),p.getElement());
            posts.add(post);
        }
        var postsBetween = new ArrayList<Post>();
        for ( Post p: posts
             ) {
            if (timeFrom <= p.timestamp && p.timestamp <= timeTo ){
                postsBetween.add(p);
            }
        }
        return postsBetween;
    }
}
