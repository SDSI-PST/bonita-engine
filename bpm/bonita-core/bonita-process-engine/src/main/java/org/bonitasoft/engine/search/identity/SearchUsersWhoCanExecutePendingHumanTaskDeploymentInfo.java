/**
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.engine.search.identity;

import java.util.List;

import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.identity.model.SUser;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.persistence.SBonitaSearchException;
import org.bonitasoft.engine.search.AbstractUserSearchEntity;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.descriptor.SearchUserDescriptor;

/**
 * @author Julien Reboul
 * 
 */
public class SearchUsersWhoCanExecutePendingHumanTaskDeploymentInfo extends AbstractUserSearchEntity {

    private final ActivityInstanceService activityInstanceService;

    private long humanTaskInstanceId;

    public SearchUsersWhoCanExecutePendingHumanTaskDeploymentInfo(long humanTaskInstanceId, final ActivityInstanceService activityInstanceService,
            final SearchUserDescriptor searchDescriptor, final SearchOptions options) {
        super(searchDescriptor, options);
        this.activityInstanceService = activityInstanceService;
        this.humanTaskInstanceId = humanTaskInstanceId;
    }

    @Override
    public long executeCount(final QueryOptions searchOptions) throws SBonitaSearchException {
        return activityInstanceService.getNumberOfUsersWhoCanExecutePendingHumanTaskDeploymentInfo(humanTaskInstanceId, searchOptions);
    }

    @Override
    public List<SUser> executeSearch(final QueryOptions searchOptions) throws SBonitaSearchException {
        return activityInstanceService.searchUsersWhoCanExecutePendingHumanTaskDeploymentInfo(humanTaskInstanceId, searchOptions);
    }

}
