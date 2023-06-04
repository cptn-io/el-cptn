import pluralize from "pluralize";

const renderNoMatchingRecordsWithFilter = (type) => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="No matching records" src="/undraw/void.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            {`There are no ${pluralize(type)} matching the current filters.`}
        </div>
    </div>
}

export { renderNoMatchingRecordsWithFilter };