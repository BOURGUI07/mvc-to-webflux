package com.example.analytics_service.unit_tests;

import com.example.analytics_service.repo.ProductViewRepo;
import com.example.analytics_service.service.ProductViewService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AbstractUnitTest {
    @Mock
    protected ProductViewRepo repo;

    @InjectMocks
    protected ProductViewService service;
}
