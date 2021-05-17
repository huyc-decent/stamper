package com.yunxi.stamper.service;

import com.yunxi.stamper.entity.StrategyPassword;

public interface StrategyPasswordService {
    StrategyPassword getByOrg(Integer orgId);

    void add(StrategyPassword sp);

    void update(StrategyPassword strategy);

}
