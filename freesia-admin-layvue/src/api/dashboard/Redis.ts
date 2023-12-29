import Http from "../Http";

export function findRedisDashboardInfo() {
    return Http.get("/dashboard/redisDashboardController/findRedisDashboardInfo");
}
