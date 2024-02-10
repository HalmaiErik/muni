import { useRef, useState } from "react";
import { CategoryDto, ConditionDto } from "../../api/dtos"
import { Button, FormControl, IconButton, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import ClearIcon from '@mui/icons-material/Clear';
import DoneIcon from '@mui/icons-material/Done';

type Props = {
    condition?: ConditionDto;
    addCondition: (arg0: ConditionDto) => void;
};

const ConditionForm = ({ condition, addCondition }: Props) => {
    const [column, setColumn] = useState(condition?.transactionColumn || '');
    const [operation, setOperation] = useState(condition?.operation || '');
    const [value, setValue] = useState(condition?.value || '');

    const save = (event: React.FormEvent) => {
        event.preventDefault();

        addCondition({
            id: condition?.id,
            transactionColumn: column,
            operation,
            value
        });
    }

    const changed = () => {
        if (condition?.transactionColumn !== column || condition?.operation !== operation || condition?.value !== value) {
            return true;
        }

        return false;
    }

    return (
        <form style={{ display: 'flex' }} onSubmit={save}>
            <FormControl sx={{ minWidth: 140, marginRight: '8px' }}>
                <InputLabel id="column-label">Column</InputLabel>
                <Select autoWidth labelId="column-label" label="Column" value={column} onChange={(e) => setColumn(e.target.value)} required>
                    <MenuItem value={'AMOUNT'}>{'Amount'}</MenuItem>
                    <MenuItem value={'REMITTANCE_INFO'}>{'Information'}</MenuItem>
                </Select>
            </FormControl>

            <FormControl sx={{ minWidth: 140, marginRight: '8px' }}>
                <InputLabel id="operation-label">Operation</InputLabel>
                <Select autoWidth labelId="operation-label" label="Operation" value={operation} onChange={(e) => setOperation(e.target.value)} required>
                    <MenuItem value={'EQUALS'}>{'equals'}</MenuItem>
                    <MenuItem value={'CONTAINS'}>{'contains'}</MenuItem>
                </Select>
            </FormControl >

            <TextField sx={{ marginRight: '8px' }} fullWidth required label="Value" value={value} onChange={(e) => setValue(e.target.value)} />

            {changed() &&
                <IconButton size="large" type="submit" onSubmit={save}>
                    <DoneIcon fontSize="inherit" />
                </IconButton>
            }

            <IconButton size="large">
                <ClearIcon fontSize="inherit" />
            </IconButton>
        </form>
    );
};

export default ConditionForm;