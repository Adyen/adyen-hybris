import React, {useState, useEffect} from 'react';
import axios from 'axios';

function PostRequest() {
    const [responseData, setResponseData] = useState('');

    const rootElement = document.getElementById('root');
    const contextPath = rootElement.getAttribute('context-path');
    const CSRFToken = rootElement.getAttribute('csrf-token');

    useEffect(() => {
        axios.post(contextPath + '/checkoutApi/postTest', {
            testParam: 'testValue'
        }, {
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
                'Accept': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then(response => {
                setResponseData(response.data);
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }, []);

    return (
        <div>
            <h2>Response from POST Request:</h2>
            <p>{responseData}</p>
        </div>
    );
}

export default PostRequest;