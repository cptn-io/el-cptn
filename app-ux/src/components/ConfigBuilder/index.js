import { IconEye, IconEyeOff, IconTrash, IconX } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import filter from 'lodash/filter';

const ConfigBuilder = (props) => {
    const { config, setConfig, readOnly } = props;
    const [visibleFields, setVisibleFields] = useState({});

    useEffect(() => {
        setConfig(current => {
            if (current.length === 0 || current[current.length - 1].key || current[current.length - 1].value) {
                return [...current, { key: "", value: "", secret: false }]
            }
            return current;
        });
    }, [config]);

    const changeConfig = (e, i) => {
        setConfig(current => {
            const updated = [...current];
            if (e.target.name === "secret") {
                updated[i][e.target.name] = e.target.checked;
            } else {
                updated[i][e.target.name] = e.target.value;
            }
            return updated;
        });
    }

    const toggleSecretVisibility = (e, key) => {
        e.preventDefault();
        setVisibleFields(current => ({
            ...current,
            [key]: !current[key]
        }));
    }

    const deleteItem = (e, i) => {
        e.preventDefault();
        setConfig(current => filter(current, (val, index) => index !== i));
    }

    return <div className="foo">
        {config.map((item, i) => {
            return <div className="flex flex-wrap -mx-3 mb-3" key={i}>
                <div className="w-1/2 md:w-2/5 px-3">
                    <input readOnly={readOnly} defaultValue={item.key} name="key" onChange={(e) => changeConfig(e, i)} className="input input-bordered w-full" type="text" placeholder="Key" />
                </div>
                <div className="w-1/2 md:w-2/5 px-3">
                    <div className="input-group">
                        <input readOnly={readOnly} type={!item.secret || visibleFields[item.key] ? "text" : "password"} name="value" value={item.value} onChange={(e) => changeConfig(e, i)} placeholder="Value" className="input input-bordered w-full" />
                        <button className="btn btn-square" disabled={!item.secret} onClick={(e) => toggleSecretVisibility(e, item.key)}>
                            {!visibleFields[item.key] && <IconEye size="24" />}
                            {visibleFields[item.key] && <IconEyeOff size="24" />}
                        </button>
                    </div>
                </div>
                <div className="w-full mt-2 md:mt-0 md:w-1/5 px-3 flex place-items-center">
                    {!readOnly && <><input type="checkbox" name="secret" checked={item.secret} onChange={(e) => changeConfig(e, i)} className="checkbox checkbox-md" />
                        <div className="mx-3">Secret</div>

                        <button className="btn btn-sm btn-error btn-square" disabled={(!item.key && !item.value)} onClick={(e) => deleteItem(e, i)}>
                            <IconTrash size="24" />
                        </button></>}
                </div>
            </div>
        })}
    </div>
}

export default ConfigBuilder;