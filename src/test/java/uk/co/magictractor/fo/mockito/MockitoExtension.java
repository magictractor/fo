/**
 * Copyright 2024 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.fo.mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

/**
 *
 */
public class MockitoExtension implements AfterEachCallback {

    private boolean verifyNoMoreInteractions = true;

    private final List<Object> mocks = new ArrayList<>();

    public MockitoExtension verifyNoMoreInteractions(boolean verifyNoMoreInteractions) {
        this.verifyNoMoreInteractions = verifyNoMoreInteractions;
        return this;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (verifyNoMoreInteractions && !mocks.isEmpty()) {
            Mockito.verifyNoMoreInteractions(mocks.toArray());
        }
    }

    private <T> T mock0(Supplier<T> mockSuppier) {
        T mock = mockSuppier.get();
        mocks.add(mock);
        return mock;
    }

    public <T> T mock(Supplier<T> mockSuppier) {
        T mock = mock0(mockSuppier);
        if (!Mockito.mockingDetails(mock).isMock()) {
            throw new IllegalArgumentException("Supplier does not supply a mock object");
        }
        return mock;
    }

    public <T> T mock(Class<T> classToMock) {
        return mock0(() -> Mockito.mock(classToMock));
    }

}
