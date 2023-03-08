import axios from "axios";
import { useEffect, Fragment, useState } from "react";
import Loading from "../Loading";
import get from "lodash/get";
import ReactDOM from "react-dom";
import useNotifications from "../../hooks/useNotifications";
import Pagination from "../Pagination";
import { IconCheck, IconX } from '@tabler/icons-react';

const renderColData = (val) => {
    if (typeof val === 'boolean') {
        return <div className="flex justify-center items-center">{val ? <IconCheck size={24} /> : <IconX size={24} />}</div>;
    }
    return val;
}

const ItemSelectorModal = (props) => {
    const { addNotification } = useNotifications();
    const { title, url, cols, onCancel, onSelect } = props;
    const [opened, setOpened] = useState(true);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);
    const [data, setData] = useState([]);

    const renderRow = (item, cols) => {
        const { id, name } = item;
        return <tr key={id}>
            {cols.map((col, i) => {
                return <td key={col.name} className={`whitespace-pre-wrap break-words ${col.datacss}`}>{i === 0 ? <button onClick={() => onSelect({ id, name })} className="btn btn-link btn-xs">{renderColData(item[col.name])}</button> : renderColData(item[col.name])}</td>
            })}
            {/* <td><div className="flex justify-center items-center"><button onClick={() => onSelect({ id, name })} className="btn btn-primary btn-xs">Select</button></div></td> */}
        </tr>
    }

    useEffect(() => {
        axios.get(`${url}?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching data'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [url, page, addNotification]);


    useEffect(() => {
        const close = (e) => {
            if (e.key === 'Escape') {
                onCancel();
            }
        }
        window.addEventListener('keydown', close)
        return () => window.removeEventListener('keydown', close)
    }, [onCancel])

    useEffect(() => {
        if (!opened) {
            onCancel();
        }
    }, [opened, onCancel])

    return ReactDOM.createPortal(<Fragment>
        <input type="checkbox" id="my-modal-4" defaultChecked={opened} onChange={(e) => setOpened(e.target.checked)} className="modal-toggle" />
        <div className="modal">
            <div className="modal-box relative w-11/12 max-w-3xl">
                <label htmlFor="my-modal-4" className="btn btn-sm btn-circle absolute right-2 top-2">✕</label>
                <h3 className="font-bold text-lg">{title}</h3>
                {loading ? <Loading /> : <Fragment>
                    <div className="mt-3" style={{ height: "325px", overflowY: "scroll" }}>
                        <table className="table table-compact table-zebra w-full">
                            <thead>
                                <tr>
                                    {cols.map(col => {
                                        return <th key={col.name} className={`bg-base-300 ${col.css}`}>{col.label}</th>
                                    })}
                                    {/* <th className="bg-base-300"></th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {data.map((item, i) => renderRow(item, cols))}
                            </tbody>
                        </table>
                    </div>
                    <Pagination currentPage={page} setCurrentPage={setPage} totalCount={totalCount} />

                </Fragment>}

            </div>

        </div>

    </Fragment>, document.querySelector("#modal"));
}

export default ItemSelectorModal;