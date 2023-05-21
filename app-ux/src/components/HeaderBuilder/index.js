import { IconTrash } from "@tabler/icons-react";
import { useEffect } from "react";
import filter from 'lodash/filter';

const HeaderBuilder = (props) => {
    const { headers, setHeaders, readOnly } = props;

    useEffect(() => {
        setHeaders(current => {
            if (headers.length === 0 || filter(headers, (item) => !item.key || !item.value).length === 0) {
                return [...current, { key: "", value: "" }]
            }
            return current;
        });
    }, [headers, setHeaders]);

    const changeHeader = (e, i) => {
        setHeaders(current => {
            const updated = [...current];
            updated[i][e.target.name] = e.target.value;
            return updated;
        });
    }


    const deleteItem = (e, i) => {
        e.preventDefault();
        setHeaders(current => {
            const updated = [...current];
            updated.splice(i, 1);
            return updated;
        });
    };


    return <div>
        {headers.map((item, i) => {
            return <div className="flex flex-wrap -mx-3 mb-3" key={item.key + item.value}>
                <div className="w-1/2 md:w-2/5 px-3">
                    <input readOnly={readOnly} value={item.key} name="key" onChange={(e) => changeHeader(e, i)} className="input input-bordered w-full" type="text" placeholder="Key" />
                </div>
                <div className="w-1/2 md:w-2/5 px-3">
                    <input readOnly={readOnly} type="text" name="value" value={item.value} onChange={(e) => changeHeader(e, i)} placeholder="Value" className="input input-bordered w-full" />
                </div>
                <div className="w-full mt-2 md:mt-0 md:w-1/5 px-3 flex place-items-center">
                    {!readOnly &&
                        <button className="btn btn-sm btn-error btn-square" disabled={(!item.key && !item.value)} onClick={(e) => deleteItem(e, i)}>
                            <IconTrash size="24" />
                        </button>}
                </div>
            </div>
        })}
    </div>
}

export default HeaderBuilder;