package com.mikorpar.brbljavac_api.services;

import com.mikorpar.brbljavac_api.data.dtos.groups.MessagesAndUsersDTO;
import com.mikorpar.brbljavac_api.data.dtos.messages.MsgCreateReqDTO;
import com.mikorpar.brbljavac_api.data.dtos.messages.MsgWithUserDTO;
import com.mikorpar.brbljavac_api.data.models.Group;
import com.mikorpar.brbljavac_api.data.models.Message;
import com.mikorpar.brbljavac_api.data.repositories.GroupRepository;
import com.mikorpar.brbljavac_api.exceptions.groups.GroupNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.users.UserNotGroupMemberException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final GroupRepository groupRepository;
    private final MongoTemplate mongoTemplate;

    public List<MsgWithUserDTO> getGroupMessages(String gid, String uid)
            throws UserNotGroupMemberException, GroupNotFoundException {
        MessagesAndUsersDTO msgsAndUsers = groupRepository.findMsgsAndUsersByGroupId(gid);
        validateMsgsAndUsers(msgsAndUsers, gid, uid);

        Query query = new Query(Criteria.where("_id"));
        Update update = new Update().addToSet("last_message_seeners", new ObjectId(uid));
        mongoTemplate.updateFirst(query, update, Group.class);

        return msgsAndUsers.getMessages();
    }

    public Group addMessageToGroup(MsgCreateReqDTO msg, String gid, String uid)
            throws GroupNotFoundException, UserNotGroupMemberException {
        Group group = groupRepository.findById(gid).orElse(null);
        validateGroup(group, gid, uid);

        group.setUpdatedAt(Instant.now());
        group.getMessages().add(new Message(
                uid,
                msg.getText(),
                Instant.now(),
                msg.getRepliedOn(),
                msg.getFileId()
        ));

        return groupRepository.save(group);
    }

    public void deleteMessage(String msgId, String gid, String uid)
            throws UserNotGroupMemberException, GroupNotFoundException {
        Group group = groupRepository.findById(gid).orElse(null);
        validateGroup(group, gid, uid);
        
        group.getMessages()
                .stream()
                .filter(msg -> msg.getId().equals(msgId) && msg.getUserId().equals(uid))
                .findFirst()
                .ifPresent(msg -> {
                    group.getMessages().remove(msg);
                    group.setUpdatedAt(Instant.now());
                    groupRepository.save(group);
                });
    }

    private void validateGroup(Group group, String gid, String uid)
            throws GroupNotFoundException, UserNotGroupMemberException {
        validateObject(group, gid);
        validateGroupUsers(group.getUsers(), gid, uid);
    }

    private void validateMsgsAndUsers(MessagesAndUsersDTO msgsAndUsers, String gid, String uid)
            throws GroupNotFoundException, UserNotGroupMemberException {
        validateObject(msgsAndUsers, gid);
        validateGroupUsers(msgsAndUsers.getUsers(), gid, uid);
    }

    private void validateObject(Object object, String gid) throws GroupNotFoundException {
        if (object == null) {
            throw new GroupNotFoundException(String.format("Group with id '%s' does not exist", gid));
        }
    }

    private void validateGroupUsers(List<String> userIds, String gid, String uid) throws UserNotGroupMemberException {
        if (userIds.stream().noneMatch(userId -> userId.equals(uid))) {
            throw new UserNotGroupMemberException(
                    String.format("Group with id '%s' doesn't contain current user", gid)
            );
        }
    }
}