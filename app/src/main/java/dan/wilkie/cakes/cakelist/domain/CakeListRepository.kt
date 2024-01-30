package dan.wilkie.cakes.cakelist.domain

import dan.wilkie.cakes.common.domain.Repository
import kotlinx.coroutines.delay

class CakeListRepository(private val cakeService: CakeListService): Repository<List<Cake>>(
  request = {
    delay(1000)
    cakeService.cakes()
  }
)