package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.UserManagement;
import dk.cphbusiness.mrv.twitterclone.dto.UserCreation;
import dk.cphbusiness.mrv.twitterclone.dto.UserOverview;
import dk.cphbusiness.mrv.twitterclone.dto.UserUpdate;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserManagementImpl implements UserManagement {

    private Jedis jedis;

    public UserManagementImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean createUser(UserCreation userCreation) {

       if(jedis.exists("user:" + userCreation.username)) {
           return false;
       }
        jedis.hset( "user:" + userCreation.username, Map.of(
                "username", userCreation.username,
                "firstname", userCreation.firstname,
                "lastname", userCreation.firstname,
                "passwordHash", userCreation.passwordHash,
                "birhday", userCreation.birthday,
                "numFollowers", "0",
                "numFollowing", "0"
        ));

        return true;
    }

    @Override
    public UserOverview getUserOverview(String username) {


        Map<String,String> user = jedis.hgetAll("user" + username);

        if (user.isEmpty()){
            return null;
        }


        return new UserOverview( jedis.hget("user", "username"),
                jedis.hget("user", "firstname"),
                jedis.hget("user", "lastname"),
                Integer.parseInt(jedis.hget("user", "numFollowers")),
                Integer.parseInt(jedis.hget("user", "numFollowing"))
                );
    }

    @Override
    public boolean updateUser(UserUpdate userUpdate) {

        var user = jedis.sismember("users", userUpdate.username);
        if (!user) return false;

        Map<String,String> map = new HashMap<String, String>();
        map.put("username",userUpdate.username);
        map.put("firstname", userUpdate.firstname);
        map.put("lastname", userUpdate.lastname);
        map.put("birthday", userUpdate.birthday);

        jedis.hmset("user" + getUserOverview(userUpdate.username).username, map);
        return true;
    }

    @Override
    public boolean followUser(String username, String usernameToFollow) {
        /*
        var userToF = jedis.sismember("users", usernameToFollow);
        if (!userToF) return false;

        var user = jedis.sismember("users", usernameToFollow);
        if (!user) return false;
*/
        if(!jedis.hexists("users", usernameToFollow)) {
            return false;
        }

        if(!jedis.hexists("users", username)) {
            return false;
        }

        jedis.hincrBy("user" + username, "numFollowing", 1);
        jedis.hincrBy("user" + usernameToFollow, "numFollowers", 1);

       // jedis.hset("user" + username,"following", usernameToFollow);
        jedis.sadd(username + ":following", usernameToFollow);
        jedis.sadd(usernameToFollow+":followed", username);
      // jedis.hset("user" + usernameToFollow,"followed", username);

        return true;
    }

    @Override
    public boolean unfollowUser(String username, String usernameToUnfollow) {
        if(!jedis.hexists("users", username)) {
            return false;
        }

        try (var tran = jedis.multi()) {
            tran.hincrBy("user:"+usernameToUnfollow,"numFollowers",-1);
            tran.hincrBy("user:"+username,"numFollowing",-1);

            tran.srem(usernameToUnfollow+":followed", username);
            tran.srem(username + ":following", usernameToUnfollow);
            tran.exec();
        }

        return true;
    }

    @Override
    public Set<String> getFollowedUsers(String username) {
        if(!jedis.hexists("users", username)) {
            return null;
        }

        return (Set<String>) jedis.hmget(username+":followers");

    }

    @Override
    public Set<String> getUsersFollowing(String username) {
        if(!jedis.hexists("users", username)) {
            return null;
        }

        return (Set<String>) jedis.hmget(username+":following");
    }

}
