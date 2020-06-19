package main.java.io;

import main.java.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRaf extends RAF<User> {
    public static final String ADDRESS = "";

    public static final int POSTS_SIZE_LEN = 4,
            FOLLOWINGS_SIZE_LEN = 4,
            FOLLOWERS_SIZE_LEN = 4;


    public UserRaf() {
        super(ADDRESS);
    }

    @Override
    public void add(User obj) throws IOException {
        // id recordLen username password name  bio   postsSize followingsSize followersSize
        // 4  4         2+len    2+len    2+len 2+len 4         4              4
        getRaf().seek(getEndOfRaf());
        getRaf().writeInt(obj.getId()); // TODO: 6/19/2020 need id
        getRaf().writeInt(getRecordLen(obj));
        getRaf().writeUTF(obj.getUsername());
        getRaf().writeUTF(obj.getPassword());
        getRaf().writeUTF(obj.getName());
        getRaf().writeUTF(obj.getBio());
        getRaf().writeInt(obj.getPostsSize());
        getRaf().writeInt(obj.getFollowersSize());
        getRaf().writeInt(obj.getFollowersSize());
    }

    public ArrayList<User> getAllUsers(PostRaf postRaf , UserRelationRaf userRelationRaf) throws IOException {
        ArrayList<User> list = new ArrayList<>();
        getRaf().seek(0);

        while (getRaf().getFilePointer() < getRaf().length()) {
            int id = getRaf().readInt();
            getRaf().skipBytes(4);// skip recordLen
            User user = getUserInfo(id, postRaf, userRelationRaf);
            list.add(user);
        }

        return list;
    }

    public ArrayList<User> getUserFromList(List<Integer> followingsOfUserIds, PostRaf postRaf , UserRelationRaf userRelationRaf) throws IOException {
        return getAllUsers(postRaf , userRelationRaf).stream()
                .filter(user -> followingsOfUserIds.contains(user.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public User getUserById(int userId, PostRaf postRaf , UserRelationRaf userRelationRaf) throws IOException {
        getRaf().seek(0);

        while (getRaf().getFilePointer() < getRaf().length()) {
            int id = getRaf().readInt();
            int recordLen = getRaf().readInt();
            if (id != userId) {
                getRaf().skipBytes(recordLen); // skip record
                continue;
            }

            return getUserInfo(id, postRaf, userRelationRaf);
        }

        return null;
    }

    private User getUserInfo(int id, PostRaf postRaf , UserRelationRaf userRelationRaf) throws IOException {
        String username = getRaf().readUTF();
        String password = getRaf().readUTF();
        String name = getRaf().readUTF();
        String bio = getRaf().readUTF();
        int postsSize = postRaf.getUserPostsSize(id);
        int followingSize = userRelationRaf.userFollowingsCount(id);
        int followerSize = userRelationRaf.userFollowersCount(id);
        User user = new User(id , name , username , password , bio);
        user.setSize(followingSize , followerSize , postsSize);
        return user;
    }

    private int getRecordLen(User obj) {
        // id recordLen username password name  bio   postsSize followingsSize followersSize
        // 4  4         2+len    2+len    2+len 2+len 4         4              4
        return 2 + obj.getUsername().length() +
                2 + obj.getPassword().length() +
                2 + obj.getName().length() +
                2 + obj.getBio().length() +
                POSTS_SIZE_LEN + FOLLOWINGS_SIZE_LEN + FOLLOWERS_SIZE_LEN;
    }
}