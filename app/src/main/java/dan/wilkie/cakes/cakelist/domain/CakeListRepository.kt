package dan.wilkie.cakes.cakelist.domain

import dan.wilkie.cakes.common.domain.Repository

class CakeListRepository(private val cakeService: CakeListService): Repository<List<Cake>>(
  request = { cakeService.cakes() }
)