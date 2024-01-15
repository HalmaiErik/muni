import { CreateRequisitionRequest, CreateCustomerRequest } from "./dtos";

const baseUrl = 'http://localhost:8080/api/v1/bankaccount';

// TODO: Extract to common place
const LOCAL_STORAGE_TOKEN_KEY = 'idToken';

const getCountryInstitutions = async (countryCode: string) => {
    const response = await fetch(baseUrl + '/institutions/' + countryCode, {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`
        }),
    });
    const data = await response.json();
    return data;
}

const createRequisition = async (request: CreateRequisitionRequest) => {
    const response = await fetch(baseUrl + '/requisitions', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
    const data = await response.json();
    return data;
}

const createCustomer = async (request: CreateCustomerRequest) => {
    const response = await fetch(baseUrl + '/customer/create', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
    const data = await response.json();
    return data;
}

// TODO: maybe need request obj for this
const getCustomerAccounts = async (email: string) => {
    const response = await fetch(baseUrl + '/accounts/' + email, {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
        }),
    });
    const data = await response.json();
    return data;
}

export { getCountryInstitutions, createRequisition, createCustomer, getCustomerAccounts };