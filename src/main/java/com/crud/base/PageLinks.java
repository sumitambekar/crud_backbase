package com.crud.base;

import com.crud.helpers.EnvPropertiesHandler;
import com.crud.pages.ComputerPage;
import com.crud.pages.ComputersPage;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

public class PageLinks {

    private static final String BASE_URL = EnvPropertiesHandler.getInstance().getProperty(EnvPropertiesHandler.BASE_URL);

    private static final Map<Class, String> PAGE_LINK_MAP;
    private static final Map<Class, String[]> DEFAULT_PARAMS;

    static {
        PAGE_LINK_MAP = ImmutableMap.<Class, String>builder()
                .put(ComputerPage.class, "/computers/%s")
                .put(ComputersPage.class, "/computers")
                .build();

        DEFAULT_PARAMS = ImmutableMap.<Class, String[]>builder()
                .put(ComputerPage.class, new String[]{"new"})
                .build();
    }

    public static String getUrl(Class pageClass, String... params) {
        String url = getUrlTemplate(pageClass);
        String[] arguments = params.length == 0 ? getDefaultArgs(pageClass) : params;
        return String.format(url, arguments);
    }

    private static String getUrlTemplate(Class pageClass) {
        return Optional.ofNullable(PAGE_LINK_MAP.get(pageClass))
                .map(relativePart -> BASE_URL + relativePart)
                .orElseThrow(() -> new IllegalArgumentException("Page class is not found!"));
    }

    private static String[] getDefaultArgs(Class pageClass) {
        return DEFAULT_PARAMS.containsKey(pageClass) ? DEFAULT_PARAMS.get(pageClass) : new String[]{};
    }
}
