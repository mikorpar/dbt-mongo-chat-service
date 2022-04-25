package com.mikorpar.brbljavac_api.services;

import com.mikorpar.brbljavac_api.data.dtos.groups.GroupCreateReqDTO;
import com.mikorpar.brbljavac_api.data.dtos.groups.GroupWithoutMessagesDTO;
import com.mikorpar.brbljavac_api.data.dtos.users.UsersDTO;
import com.mikorpar.brbljavac_api.data.models.Group;
import com.mikorpar.brbljavac_api.data.models.User;
import com.mikorpar.brbljavac_api.data.repositories.GroupRepository;
import com.mikorpar.brbljavac_api.exceptions.groups.GroupNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.groups.UserNotGroupAdminException;
import com.mikorpar.brbljavac_api.exceptions.users.UserNotGroupMemberException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MongoTemplate mongoTemplate;

    public List<GroupWithoutMessagesDTO> getAllUserGroups(String uid) {
        return groupRepository.findByUsersContaining(new ObjectId(uid));
    }

    public Group createGroup(GroupCreateReqDTO group, String uid) {
        Instant instant = Instant.now();
        group.getUsers().remove(uid);
        group.getUsers().add(0, uid);
        return groupRepository.save(new Group(
                uid,
                group.getUsers(),
                Collections.emptyList(),
                group.getName(),
                instant,
                instant,
                Collections.emptyList()
        ));
    }

    public void deleteGroup(String gid, String uid)
            throws GroupNotFoundException, UserNotGroupAdminException {
        Optional<Group> optGroup = groupRepository.findById(gid);
        validateGroup(optGroup, gid, uid);
        groupRepository.deleteById(gid);
    }

    public Group updateGroup(String gid, String name, List<String> users, String uid)
            throws GroupNotFoundException, UserNotGroupAdminException {
        Optional<Group> optGroup = groupRepository.findById(gid);
        validateGroup(optGroup, gid, uid);

        Group group = optGroup.get();
        group.setName(name);
        users.remove(uid);
        users.add(0, uid);
        group.setUsers(users);
        group.setUpdatedAt(Instant.now());

        return groupRepository.save(group);
    }

    public Group removeCurrUser(String gid, String uid)
            throws GroupNotFoundException, UserNotGroupMemberException {
        Optional<Group> optGroup = groupRepository.findById(gid);

        if (!optGroup.isPresent()){
            throw new GroupNotFoundException(String.format("Group with id: '%s' does not exist", gid));
        }
        if (optGroup.get().getUsers().stream().noneMatch(user -> user.contains(uid))) {
            throw new UserNotGroupMemberException("Current user is not group member");
        }

        Group group = optGroup.get();
        group.getUsers().remove(uid);

        if (group.getUsers().isEmpty()) {
            groupRepository.deleteById(group.getId());
            return null;
        } else if (group.getAdmin().equals(uid)) {
            group.setAdmin(group.getUsers().get(0));
            return groupRepository.save(group);
        }

        return null;
    }

    public void removeUserFromAllGroups(String uid) {
        ObjectId oid = new ObjectId(uid);
        Query query = new Query(Criteria.where("users").in(oid));
        Update update = new Update().pull("users", oid);
        mongoTemplate.updateMulti(query, update, Group.class);
    }

    public List<String> getGroupMemberUsernames(String id) throws GroupNotFoundException {
        Optional<UsersDTO> users = groupRepository.findGroupUsers(id);
        if (!users.isPresent()) {
            throw new GroupNotFoundException(String.format("Group with id: '%s' does not exist", id));
        }
        return users.get().getUsers().stream().map(User::getUsername).collect(Collectors.toList());
    }

    private void validateGroup(Optional<Group> optGroup, String gid, String uid)
            throws GroupNotFoundException, UserNotGroupAdminException {
        if (!optGroup.isPresent()){
            throw new GroupNotFoundException(String.format("Group with id: '%s' does not exist", gid));
        }
        if (!optGroup.get().getAdmin().equals(uid)) {
            throw new UserNotGroupAdminException("Current user does not have permission to modify group");
        }
    }
}