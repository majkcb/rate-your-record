import { ReleaseSummary } from "./release-summary.model";
import { Genre } from "./genre.model";

export interface HomeData {
    releases: ReleaseSummary[];
    genres: Genre[];
    search: string;
    genre: string;
}
