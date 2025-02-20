import Http from "../Http";

export function findGiteeCommits() {
    return Http.get("/dashboard/giteeController/findGiteeCommits");
}
