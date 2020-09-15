/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2020 the original author or authors.
 */

package com.github.meixuesong.aggregatepersistence;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * This deepcopier use Apache common lang to clone object. Objects need to implement Serializable interface
 * Use this deepcopier if your entity has no default constructor and setter methods.
 * This deepcopier has been the default deepcopier since 1.1.0
 *
 * @author meixuesong
 */
public class SerializableDeepCopier implements DeepCopier{
    @Override
    public <T> T copy(T object) {
        if (object instanceof Serializable) {
            try {
                return (T) SerializationUtils.clone((Serializable) object);
            } catch (SerializationException exception) {
                throw new IllegalArgumentException(String.format("%s should be a serializable object.", object.getClass().getName()), exception);
            }
        }
        throw new IllegalArgumentException(String.format("%s should be a serializable object.", object.getClass().getName()));
    }
}
