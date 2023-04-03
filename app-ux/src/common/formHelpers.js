export const renderErrors = (errors, field) => {
    if (!errors || !errors.fieldErrors || !errors.fieldErrors[field] || errors.fieldErrors[field].length === 0) {
        return null;
    } else {
        return errors.fieldErrors[field].map(msg => <label key={`${msg}`} className="label">
            <span className="label-text-alt text-error">{msg}</span>
        </label>)
    }

}