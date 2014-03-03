package org.bonitasoft.engine.core.process.instance.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bonitasoft.engine.test.persistence.builder.ActorBuilder.anActor;
import static org.bonitasoft.engine.test.persistence.builder.ActorMemberBuilder.anActorMember;
import static org.bonitasoft.engine.test.persistence.builder.PendingActivityMappingBuilder.aPendingActivityMapping;
import static org.bonitasoft.engine.test.persistence.builder.UserBuilder.aUser;
import static org.bonitasoft.engine.test.persistence.builder.UserMembershipBuilder.aUserMembership;

import java.util.List;

import org.bonitasoft.engine.actor.mapping.model.SActor;
import org.bonitasoft.engine.identity.model.SUser;
import org.bonitasoft.engine.test.persistence.repository.ProcessInstanceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testContext.xml" })
@Transactional
public class ProcessInstanceQueriesTest {

    @Autowired
    private ProcessInstanceRepository repository;
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_should_return_users_mapped_with_by_user_id_in_penging_activities() throws Exception {
        SUser expectedUser = repository.add(aUser().withId(1L).build());
        repository.add(aUser().withId(2L).build()); // not expected user
        SPendingActivityMapping pendingActivity = repository.add(aPendingActivityMapping().withUserId(expectedUser.getId()).build());
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(pendingActivity.getActivityId());
        
        assertThat(userIds).containsOnly(expectedUser.getId());
    }
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_should_return_users_mapped_by_a_user_based_actormember_to_pending_activity() throws Exception {
        SActor actor = repository.add(anActor().build());
        SPendingActivityMapping addedPendingMapping = repository.add(aPendingActivityMapping().withActorId(actor.getId()).build());
        SUser expectedUser = repository.add(aUser().withId(1L).build());
        repository.add(anActorMember().forActor(actor).withUserId(expectedUser.getId()).build());
        repository.add(aUser().withId(2L).build()); // not expected user
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(addedPendingMapping.getActivityId());
        
        assertThat(userIds).containsOnly(expectedUser.getId());
    }
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_should_return_users_mapped_by_a_group_based_actormember_to_pending_activity() throws Exception {
        long aGroupId = 999L;
        long anotherGroupId = 77L;
        long aRoleId = 5L;
        SActor actor = repository.add(anActor().build());
        SPendingActivityMapping addedPendingMapping = repository.add(aPendingActivityMapping().withActorId(actor.getId()).build());
        repository.add(anActorMember().forActor(actor).withGroupId(aGroupId).build());
        SUser expectedUser = repository.add(aUser().withId(1L).build());
        repository.add(aUserMembership().forUser(expectedUser).memberOf(aGroupId, aRoleId).build());
        SUser notExpectedUser = repository.add(aUser().withId(2L).build()); 
        repository.add(aUserMembership().forUser(notExpectedUser).memberOf(anotherGroupId, aRoleId).build());
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(addedPendingMapping.getActivityId());
        
        assertThat(userIds).containsOnly(expectedUser.getId());
    }
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_should_return_users_mapped_by_a_role_based_actormember_to_pending_activity() throws Exception {
        SActor actor = repository.add(anActor().build());
        SPendingActivityMapping addedPendingMapping = repository.add(aPendingActivityMapping().withActorId(actor.getId()).build());
        long aRoleId = 999L;
        long anotheraRoleId = 4L;
        long aGrouId = 1L;
        repository.add(anActorMember().forActor(actor).withRoleId(aRoleId).build());
        SUser expectedUser = repository.add(aUser().withId(1L).build());
        repository.add(aUserMembership().forUser(expectedUser).memberOf(aGrouId, aRoleId).build());
        SUser notexpectedUser = repository.add(aUser().withId(2L).build());
        repository.add(aUserMembership().forUser(notexpectedUser).memberOf(aGrouId, anotheraRoleId).build());
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(addedPendingMapping.getActivityId());
        
        assertThat(userIds).containsOnly(expectedUser.getId());
    }
    
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_should_return_users_mapped_by_a_membership_based_actormember_to_pending_activity() throws Exception {
        SUser addedUser = repository.add(aUser().withId(1L).build());
        SUser user2 = repository.add(aUser().withId(2L).build());
        SUser user3 = repository.add(aUser().withId(3L).build());
        SUser user4 = repository.add(aUser().withId(4L).build());
        SActor actor = repository.add(anActor().build());
        long aRoleId = 999L;
        long aGroupId = 888L;
        long anotherGroupId = 777L;
        long anotherRoleId = 546L;
        repository.add(anActorMember().forActor(actor).withRoleId(aRoleId).withGroupId(aGroupId).build());
        repository.add(aUserMembership().forUser(addedUser).memberOf(aGroupId, aRoleId).build());
        repository.add(aUserMembership().forUser(user2).memberOf(anotherGroupId, aRoleId).build());
        repository.add(aUserMembership().forUser(user3).memberOf(aGroupId, anotherRoleId).build());
        repository.add(aUserMembership().forUser(user4).memberOf(aGroupId, aRoleId).build());
        SPendingActivityMapping addedPendingMapping = repository.add(aPendingActivityMapping().withActorId(actor.getId()).build());
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(addedPendingMapping.getActivityId());
        
        assertThat(userIds).containsOnly(1L, 4L);
    }
    
    @Test
    public void getPossibleUserIdsOfPendingTasks_return_userIds_ordered_by_userName() throws Exception {
        SUser john = repository.add(aUser().withUserName("john").withId(1L).build());
        SUser paul = repository.add(aUser().withUserName("paul").withId(2L).build());
        SUser walter = repository.add(aUser().withUserName("walter").withId(3L).build());
        SUser marie = repository.add(aUser().withUserName("marie").withId(4L).build());
        
        long aRoleId = 999L;
        long aGroupId = 888L;
        SActor actor = repository.add(anActor().build());
        repository.add(anActorMember().forActor(actor).withRoleId(aRoleId).withGroupId(aGroupId).build());
        SPendingActivityMapping addedPendingMapping = repository.add(aPendingActivityMapping().withActorId(actor.getId()).build());
        
        repository.add(aUserMembership().forUser(john).memberOf(aGroupId, aRoleId).build());
        repository.add(aUserMembership().forUser(paul).memberOf(aGroupId, aRoleId).build());
        repository.add(aUserMembership().forUser(walter).memberOf(aGroupId, aRoleId).build());
        repository.add(aUserMembership().forUser(marie).memberOf(aGroupId, aRoleId).build());
        
        List<Long> userIds = repository.getPossibleUserIdsOfPendingTasks(addedPendingMapping.getActivityId());
        
        assertThat(userIds).containsExactly(john.getId(), marie.getId(), paul.getId(), walter.getId());
    }
    
    
}
