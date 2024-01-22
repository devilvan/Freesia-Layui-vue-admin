export interface GiteeCommitsResponseDto {
    url?: string;
    sha?: string;
    htmlUrl?: string;
    commit?: Commit;
    author?: Author;
}

export interface Author {
    id?: string;
    login?: string;
    name?: string;
    date?: string;
    avatarUrl?: string;
    url?: string;
    htmlUrl?: string;
    remark?: string;
    type?: string;
}

export interface Commit {
    author?: CommitAuthor;
    message?: string;
}

export interface CommitAuthor {
    name?: string;
    date?: string;
    email?: string;
}
