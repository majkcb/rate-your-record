import { ReleaseSummary } from "./release-summary.model";
import { Track } from "./track.model";

export interface Release extends ReleaseSummary {
    description: string;
    numberOfRatings: number;
    tracklist: Track[];
}
