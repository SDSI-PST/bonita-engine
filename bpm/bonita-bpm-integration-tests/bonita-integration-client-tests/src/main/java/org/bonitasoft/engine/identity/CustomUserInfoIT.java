/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bonitasoft.engine.CommonAPITest;
import org.bonitasoft.engine.api.CustomUserInfoAPI;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class CustomUserInfoIT extends CommonAPITest {
    
    private static String DEFAULT_NAME = "Skills";

    private User user;

    @Before
    public void before() throws BonitaException {
        login();
        user = getIdentityAPI().createUser("john", "doe");
    }

    @After
    public void after() throws BonitaException {
        int pageSize = 20;
        List<CustomUserInfoDefinition> definitions;
        do {
            definitions = getIdentityAPI().getCustomUserInfoDefinitions(0, pageSize);
            deleteUserInfo(definitions);
        } while (definitions.size() == pageSize);
        getIdentityAPI().deleteUser(user.getId());
        logout();
    }
    
    @Cover(classes = { CustomUserInfoDefinition.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info definition", "Creation" })
    @Test
    public void createCustomUserInfoDefinition_should_return_the_new_created_object() throws Exception {
        //given
        CustomUserInfoDefinitionCreator creator = new CustomUserInfoDefinitionCreator(DEFAULT_NAME, "The user skills.");

        //when
        CustomUserInfoDefinition info = getIdentityAPI().createCustomUserInfoDefinition(creator);

        //then
        assertThat(info.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(info.getDescription()).isEqualTo("The user skills.");
    }
    
    @Cover(classes = { CustomUserInfoDefinition.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info definition", "get list" })
    @Test
    public void getCustomUserInfoDefinitions_return_objects_according_to_pagination_size_and_ordered_by_name_asc() throws Exception {
        //given
        CustomUserInfoDefinition skills = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator(DEFAULT_NAME));
        CustomUserInfoDefinition a = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("A"));
        CustomUserInfoDefinition b = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("B"));
        int pagegSize = 2;
        
        //when
        List<CustomUserInfoDefinition> definitions = getIdentityAPI().getCustomUserInfoDefinitions(0, pagegSize);
        
        //then (first page)
        assertThat(definitions).containsExactly(a, b);
        
        //when
        definitions = getIdentityAPI().getCustomUserInfoDefinitions(2, pagegSize);
        
        //then (second page)
        assertThat(definitions).containsExactly(skills);
    }

    @Cover(classes = { CustomUserInfo.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info", "get list" })
    @Test
    public void getCustomUserInfo_should_return_all_info_even_when_value_is_null() throws Exception {
        //given
        CustomUserInfoDefinition job = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("job"));
        CustomUserInfoDefinition skill = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("skill"));
        getIdentityAPI().setCustomUserInfoValue(skill.getId(), user.getId(), "java");

        //when
        List<CustomUserInfo> infoPage1 = getIdentityAPI().getCustomUserInfo(user.getId(), 0, 1);
        List<CustomUserInfo> infoPage2 = getIdentityAPI().getCustomUserInfo(user.getId(), 1, 1);

        //then
        assertThat(infoPage1.get(0).getDefinition()).isEqualTo(job);
        assertThat(infoPage1.get(0).getValue()).isEqualTo(null);
        assertThat(infoPage2.get(0).getDefinition()).isEqualTo(skill);
        assertThat(infoPage2.get(0).getValue()).isEqualTo("java");
    }

    @Cover(classes = { CustomUserInfo.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info", "set value" })
    @Test
    public void setCustomUserInfoValue_should_delete_CustomUserInfoValue_when_set_to_null() throws Exception {
        //given
        CustomUserInfoDefinition job = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("job"));
        getIdentityAPI().setCustomUserInfoValue(job.getId(), user.getId(), "code slayer");
        CustomUserInfoDefinition skill = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("skill"));
        getIdentityAPI().setCustomUserInfoValue(skill.getId(), user.getId(), "java");

        //when
        getIdentityAPI().setCustomUserInfoValue(skill.getId(), user.getId(), null);

        //then
        List<CustomUserInfoValue> values = getIdentityAPI().searchCustomUserInfoValues(new SearchOptionsBuilder(0, 10).done()).getResult();
        assertThat(values.get(0).getDefinitionId()).isEqualTo(job.getId());
        assertThat(values.get(0).getUserId()).isEqualTo(user.getId());
        assertThat(values.get(0).getValue()).isEqualTo("code slayer");
    }

    @Cover(classes = { CustomUserInfo.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info", "set value" })
    @Test
    public void setCustomUserInfoValue_should_update_CustomUserInfoValue_when_one_already_exist() throws Exception {
        //given
        CustomUserInfoDefinition job = getIdentityAPI().createCustomUserInfoDefinition(new CustomUserInfoDefinitionCreator("job"));
        getIdentityAPI().setCustomUserInfoValue(job.getId(), user.getId(), "code slayer");

        //when
        getIdentityAPI().setCustomUserInfoValue(job.getId(), user.getId(), "or not");

        //then
        List<CustomUserInfoValue> values = getIdentityAPI().searchCustomUserInfoValues(new SearchOptionsBuilder(0, 10).done()).getResult();
        assertThat(values.get(0).getDefinitionId()).isEqualTo(job.getId());
        assertThat(values.get(0).getUserId()).isEqualTo(user.getId());
        assertThat(values.get(0).getValue()).isEqualTo("or not");
    }

    private void deleteUserInfo(List<CustomUserInfoDefinition> definitions) throws DeletionException {
        for (CustomUserInfoDefinition definition : definitions) {
            getIdentityAPI().deleteCustomUserInfoDefinition(definition.getId());
        }
    }

    @Cover(classes = { CustomUserInfoDefinition.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info definition", "Deletion" })
    @Test
    public void deleteCustomUserInfoDefinition_should_delete_definition_and_values_from_database() throws Exception {
        //given
        CustomUserInfoDefinition info = createDefinition(DEFAULT_NAME);
        getIdentityAPI().setCustomUserInfoValue(info.getId(), user.getId(), "Java");
        assertThat(getIdentityAPI().getCustomUserInfoDefinitions(0, 10)).containsExactly(info);
        SearchOptions searchOptions = new SearchOptionsBuilder(0, 1).done();
        assertThat(getIdentityAPI().searchCustomUserInfoValues(searchOptions).getCount()).isEqualTo(1);

        //when
        getIdentityAPI().deleteCustomUserInfoDefinition(info.getId());
        List<CustomUserInfoDefinition> definitions = getIdentityAPI().getCustomUserInfoDefinitions(0, 10);
        SearchResult<CustomUserInfoValue> values = getIdentityAPI().searchCustomUserInfoValues(searchOptions);

        //then
        assertThat(definitions).isEmpty();
        assertThat(values.getCount()).isEqualTo(0);
    }

    @Cover(classes = { CustomUserInfoDefinition.class, CustomUserInfoAPI.class }, concept = BPMNConcept.ORGANIZATION, jira = "BS-7150", keywords = { "Custom user info value", "User deletion" })
    @Test
    public void deleteUser_should_delete_related_custom_user_info_value_from_database() throws Exception {
        //given
        CustomUserInfoDefinition info = createDefinition(DEFAULT_NAME);
        User user = createUser("user.with.custom.user.info", "bpm");
        getIdentityAPI().setCustomUserInfoValue(info.getId(), user.getId(), "Java");
        assertThat(getIdentityAPI().getCustomUserInfoDefinitions(0, 10)).containsExactly(info);
        SearchOptions searchOptions = new SearchOptionsBuilder(0, 1).done();
        assertThat(getIdentityAPI().searchCustomUserInfoValues(searchOptions).getCount()).isEqualTo(1);
        
        //when
        getIdentityAPI().deleteUser(user.getId());
        List<CustomUserInfoDefinition> definitions = getIdentityAPI().getCustomUserInfoDefinitions(0, 10);
        SearchResult<CustomUserInfoValue> values = getIdentityAPI().searchCustomUserInfoValues(searchOptions);
        
        //then
        assertThat(definitions.size()).isEqualTo(1);
        assertThat(values.getCount()).isEqualTo(0);
    }


    private CustomUserInfoDefinition createDefinition(String name) throws CreationException {
        CustomUserInfoDefinitionCreator creator = new CustomUserInfoDefinitionCreator(name);
        return getIdentityAPI().createCustomUserInfoDefinition(creator);
    }

}
