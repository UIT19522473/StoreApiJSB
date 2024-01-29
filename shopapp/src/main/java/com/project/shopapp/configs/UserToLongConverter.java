package com.project.shopapp.configs;

import com.project.shopapp.models.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class UserToLongConverter implements Converter<User, Long> {

    @Override
    public Long convert(MappingContext<User, Long> context) {
        return context.getSource().getId();
    }
}