export interface Genre {
    id: number;
    name: string;
    image_background: string
}

import genres from "../data/genres";


const useGenres = () => ({data: genres, isLoading: false, error: null}) // useData<Genre>("/genres");

export default useGenres;