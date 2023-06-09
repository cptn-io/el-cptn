import { IconChevronsLeft, IconChevronLeft, IconChevronRight, IconChevronsRight } from '@tabler/icons-react';
import { useState, useEffect, Fragment } from "react";
import { useSearchParams } from "react-router-dom";

const Pagination = (props) => {
    const { totalCount = 0, pageSize = 15, currentPage, setCurrentPage } = props;
    const [searchParams, setSearchParams] = useSearchParams();
    const [page, setPage] = useState(currentPage || (searchParams.has('page') ? searchParams.get('page') * 1 : 0));

    const startCount = totalCount > 0 ? page * pageSize + 1 : 0;
    const endCount = (page * pageSize + pageSize) < totalCount ? (page * pageSize + pageSize) : totalCount;

    useEffect(() => {
        if (setCurrentPage) {
            setCurrentPage(page);
            return;
        }

        searchParams.set("page", page);
        setSearchParams(searchParams);
    }, [page, searchParams, setSearchParams, setCurrentPage])

    return <div className="mt-3 p-3 flex justify-between items-center bg-base-200 rounded-xl">
        <div>
            {totalCount > 0 ? <Fragment>{`${startCount} - ${endCount} of ${totalCount} records`}</Fragment>
                : <Fragment>No Records</Fragment>
            }
        </div>
        <div className="join">
            <button disabled={page === 0} onClick={() => setPage(0)} className="btn join-item btn-sm"><IconChevronsLeft size={24} /></button>
            <button disabled={page === 0} onClick={() => setPage(page => page - 1)} className="btn join-item btn-sm"><IconChevronLeft size={24} /></button>
            <button disabled={(totalCount / pageSize - 1) <= page} onClick={() => setPage(page => page + 1)} className="btn btn-sm join-item"><IconChevronRight size={24} /></button>
            <button disabled={(totalCount / pageSize - 1) <= page} onClick={() => setPage(Math.floor(totalCount / pageSize))} className="btn btn-sm join-item"><IconChevronsRight size={24} /></button>
        </div>
    </div>
}

export default Pagination;