import Http from "../Http";

export function requestGiteeCommits() {
    return Http.get("/dashboard/giteeController/requestGiteeCommits");
}
