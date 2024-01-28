import { LOCAL_STORAGE_TOKEN_KEY } from "../constants/constants";
import { AccountDto, RequisitionDto, InstitutionDto, TransactionDto } from "./dtos";
import { CreateCustomerRequest, CreateRequisitionRequest, GetAccountTransactionsRequest } from "./requests";

const baseUrl = 'http://localhost:8080/api/v1/bankaccount';

const getCountryInstitutions = async (countryCode: string) => {
    const response = await fetch(baseUrl + '/institutions/' + countryCode, {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`
        }),
    });
    const data: InstitutionDto[] = await response.json();
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
    const data: RequisitionDto = await response.json();
    return data;
}

const createCustomer = async (request: CreateCustomerRequest) => {
    await fetch(baseUrl + '/customer/create', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
}

const getCustomerAccounts = async (email: string) => {
    const response = await fetch(baseUrl + '/accounts/' + email, {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
        }),
    });
    const data: AccountDto[] = await response.json();
    return data;
}

const getAccountTransactions = async (request: GetAccountTransactionsRequest) => {
    const response = await fetch(baseUrl + '/account/transactions', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
    const data: TransactionDto[] = await response.json();
    return data;
}

export {
    getCountryInstitutions,
    createRequisition,
    createCustomer,
    getCustomerAccounts,
    getAccountTransactions
};