package com.project.domain.usecase.challengegroup

import com.project.domain.repository.ChallengeGroupRepository
import javax.inject.Inject

class FetchChallengeGroupListUseCase @Inject constructor(
    private val repository: ChallengeGroupRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        search: String? = null,
        created: Boolean? = null,
        participating: Boolean? = null
    ) = repository.fetchChallengeGroupList(
        page = page,
        size = size,
        search = search,
        created = created,
        participating = participating
    )
}
