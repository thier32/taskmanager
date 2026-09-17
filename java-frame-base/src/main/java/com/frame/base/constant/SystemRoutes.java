package com.frame.base.constant;

public class SystemRoutes {
    public static final String BASE_ROUTE = "/api";

    public static final String FORM_FIELD_ROUTE = BASE_ROUTE + "/form/field";
    public static final String PROFILE_CATEGORY_ROUTE = BASE_ROUTE + "/profil/category";
    public static final String PROFILE_TYPE_ROUTE = BASE_ROUTE + "/profil/type";
    public static final String PERMISSION_ROUTE = BASE_ROUTE + "/permission";
    public static final String INVESTOR_ROUTE = BASE_ROUTE + "/investor";
    public static final String INVESTMENT_ROUTE = BASE_ROUTE + "/investment";
    public static final String PRODUCT_ROUTE = BASE_ROUTE + "/product";
    public static final String PORTFOLIO_ROUTE = BASE_ROUTE + "/portfolio";

    public static final String ROLE_ROUTE = BASE_ROUTE + "/role";
    public static final String USER_ROUTE = BASE_ROUTE + "/user";
    public static final String LEVEL_ROUTE = BASE_ROUTE + "/level";
    public static final String ROOM_ROUTE = BASE_ROUTE + "/room";
    public static final String SCHOOL_ROUTE = BASE_ROUTE + "/school";
    public static final String SUBJECT_ROUTE = BASE_ROUTE + "/subject";
    public static final String SYSTEM_ROUTE = BASE_ROUTE + "/system";

    public static final String USER_PROFILE_ROUTE = BASE_ROUTE + "/user/profile";

    public static final String NEW_BATCH_ROUTE = "/new/batch";
    public static final String NEW_ROUTE = "/new";
    public static final String LIST_ROUTE = "/list";
    public static final String SINGLE_ROUTE = "/display";
    public static final String DELETE_ROUTE = "/delete";
    public static final String DISABLE_ROUTE = "/disable";
    public static final String ENABLE_ROUTE = "/enable";
    public static final String DEACTIVATE_ROUTE = "/deactivate";
    public static final String ACTIVATE_ROUTE = "/activate";
    public static final String EDIT_ROUTE = "/edit";
    public static final String CLONE_ROUTE = "/clone";
    public static final String CLONE_BATCH_ROUTE = "/clone/batch";
    public static final String BATCH_UPLOAD_ROUTE = "/batch/upload";
    public static final String DTO_ROUTE = "/dto";
    public static final String DASHBOARD_ROUTE = "/dashboard";



    public static final String[] DEFAULT_ROUTES = new String[]{
            NEW_BATCH_ROUTE,
            NEW_ROUTE,
            LIST_ROUTE,
            CLONE_ROUTE,
            CLONE_BATCH_ROUTE,
            BATCH_UPLOAD_ROUTE,
            DTO_ROUTE,
            SINGLE_ROUTE,
            DASHBOARD_ROUTE,
    };

    public static boolean isBatchRoute(String route){
        return route.endsWith("batch");
    }

    public static String MONITOR_ROUTE = "/topic/monitoring";
}
