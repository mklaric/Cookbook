package com.fluxes.cookbook;


import org.json.JSONException;
import org.json.JSONObject;


public class ServerAPI {
    public static String Server = "http://cookbook.nonagon.eu";

    public static Request Login(String username, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/users/login",
                "POST",
                json
        );
    }

    public static Request GetProfile(String access_token) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/users/me",
                "GET",
                json
        );
    }

    public static Request PatchProfile(String access_token, JSONObject json) {
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/users/me",
                "PATCH",
                json
        );
    }

    public static Request GetUser(String access_token, String username) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/users/" + username,
                "GET",
                json
        );
    }

    public static Request RecipeQuery(String access_token, String... tags) {
        JSONObject json = new JSONObject();
        String tags_filter = "";

        for (int i = 0; i < tags.length; ++i) {
            if (i != 0)
                tags_filter += ",";
            tags_filter += tags[i];
        }

        try {
            json.put("access_token", access_token);
            if (!tags_filter.equals(""))
                json.put("tags", tags_filter);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/query",
                "GET",
                json
        );
    }

    public static Request GetRecipes(String access_token, int page, int limit) {
        JSONObject json = new JSONObject();

        try {
            json.put("access_token", access_token);
            json.put("page", page);
            json.put("limit", limit);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes",
                "GET",
                json
        );
    }

    public static Request GetRecipes(String access_token) {
        return ServerAPI.GetRecipes(access_token, 0, 100);
    }

    public static Request GetUserRecipes(String access_token, String username, int page, int limit) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
            json.put("page", page);
            json.put("limit", limit);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/users/" + username + "/recipes",
                "GET",
                json
        );
    }

    public static Request GetUserRecipes(String access_token, String username) {
        return ServerAPI.GetUserRecipes(access_token, username, 0, 100);
    }

    public static Request GetUserRecipes(String username) {
        return ServerAPI.GetUserRecipes(null, username);
    }

    public static Request GetRecipe(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id),
                "GET",
                json
        );
    }

    public static Request GetRecipe(int id) {
        return ServerAPI.GetRecipe(null, id);
    }

    public static Request AddRecipe(
            String access_token,
            String name,
            String description,
            Boolean _private,
            int preparation_time,
            int cooking_time
            // photo
        ) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
            json.put("name", name);
            json.put("description", description);
            json.put("private", _private);
            json.put("preparation_time", preparation_time);
            json.put("cooking_time", cooking_time);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes",
                "POST",
                json
        );
    }

    public static Request PatchRecipe(String access_token, int id, JSONObject json) {
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id),
                "PATCH",
                json
        );
    }

    public static Request DeleteRecipe(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id),
                "DELETE",
                json
        );
    }

    public static Request AddStep(String access_token, int id, String description) { // photo
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
            json.put("description", description);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/steps",
                "POST",
                json
        );
    }

    public static Request GetStep(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/steps/" + Integer.toString(id),
                "GET",
                json
        );
    }

    public static Request GetStep(int id) {
        return ServerAPI.GetStep(null, id);
    }

    public static Request DeleteStep(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/steps/" + Integer.toString(id),
                "DELETE",
                json
        );
    }

    public static Request PatchStep(String access_token, int id, JSONObject json) {
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/steps/" + Integer.toString(id),
                "PATCH",
                json
        );
    }

    public static Request PatchStep(String access_token, int id, String description) { // photo
        JSONObject json = new JSONObject();
        try {
            json.put("description", description);
        } catch (JSONException e) {}

        return ServerAPI.PatchStep(access_token, id, json);
    }

    public static Request AddIngredient(String access_token, int id, String name) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
            json.put("name", name);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/ingredients",
                "POST",
                json
        );
    }

    public static Request GetIngredient(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/ingredients/" + Integer.toString(id),
                "GET",
                json
        );
    }

    public static Request GetIngredient(int id) {
        return ServerAPI.GetIngredient(null, id);
    }

    public static Request PatchIngredient(String access_token, int id, JSONObject json) {
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/ingredients/" + Integer.toString(id),
                "PATCH",
                json
        );
    }

    public static Request PatchIngredient(String access_token, int id, String name) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
        } catch (JSONException e) {}

        return PatchIngredient(access_token, id, json);
    }

    public static Request DeleteIngredient(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/ingredients/" + Integer.toString(id),
                "DELETE",
                json
        );
    }

    public static Request GetTags() {
        return new Request(
                ServerAPI.Server + "/api/tags",
                "GET",
                new JSONObject()
        );
    }

    public static Request GetTag(String tag) {
        return new Request(
                ServerAPI.Server + "/api/tags/" + tag,
                "GET",
                new JSONObject()
        );
    }

    public static Request AddTag(String access_token, int id, String tag) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
            json.put("name", tag);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/tags",
                "POST",
                json
        );
    }

    public static Request DeleteTag(String access_token, int id, String tag) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
            json.put("name", tag);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/tags",
                "DELETE",
                json
        );
    }

    public static Request GetFavorites(String access_token) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/favorites",
                "GET",
                json
        );
    }

    public static Request GetFavorite(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/favorite",
                "GET",
                json
        );
    }

    public static Request AddFavorite(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/favorite",
                "POST",
                json
        );
    }

    public static Request DeleteFavorite(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/favorite",
                "DELETE",
                json
        );
    }

    public static Request GetGrade(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/grade",
                "GET",
                json
        );
    }

    public static Request AddGrade(String access_token, int id, int grade) {
        JSONObject json = new JSONObject();
        try {
            if (access_token != null)
                json.put("access_token", access_token);
            json.put("grade", grade);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/grade",
                "POST",
                json
        );
    }

    public static Request PatchGrade(String access_token, int id, JSONObject json) {
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/grade",
                "PATCH",
                json
        );
    }

    public static Request PatchGrade(String access_token, int id, int grade) {
        JSONObject json = new JSONObject();
        try {
            json.put("grade", grade);
        } catch (JSONException e) {}

        return ServerAPI.PatchGrade(access_token, id, json);
    }

    public static Request DeleteGrade(String access_token, int id) {
        JSONObject json = new JSONObject();
        try {
            json.put("access_token", access_token);
        } catch (JSONException e) {}

        return new Request(
                ServerAPI.Server + "/api/recipes/" + Integer.toString(id) + "/grade",
                "DELETE",
                json
        );
    }
}
