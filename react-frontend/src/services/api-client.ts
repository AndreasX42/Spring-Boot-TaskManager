import axios from "axios";

export default axios.create({
    baseURL: 'https://api.rawg.io/api',
    params: {
        key: 'b378b80294134e5a81b9c9891020ccd8'
    }
})