import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";

const useStatusFilter = ({ status }) => {
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        if (status) {
            searchParams.set("status", status);
        } else {
            searchParams.delete("status");
        }
        setSearchParams(searchParams);
    }, [status, searchParams, setSearchParams]);
}

export default useStatusFilter;